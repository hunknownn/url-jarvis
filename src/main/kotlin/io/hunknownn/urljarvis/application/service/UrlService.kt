package io.hunknownn.urljarvis.application.service

import io.hunknownn.urljarvis.adapter.`in`.web.exception.UrlNotFoundException
import io.hunknownn.urljarvis.application.port.`in`.ManageUrlUseCase
import io.hunknownn.urljarvis.application.port.`in`.RegisterUrlUseCase
import io.hunknownn.urljarvis.application.port.out.event.CrawlEventPublisher
import io.hunknownn.urljarvis.application.port.out.persistence.UrlChunkRepository
import io.hunknownn.urljarvis.application.port.out.persistence.UrlRepository
import io.hunknownn.urljarvis.domain.url.CrawlStatus
import io.hunknownn.urljarvis.domain.url.Url
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
class UrlService(
    private val urlRepository: UrlRepository,
    private val urlChunkRepository: UrlChunkRepository,
    private val crawlEventPublisher: CrawlEventPublisher
) : RegisterUrlUseCase, ManageUrlUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun register(userId: Long, url: String): Url {
        val existing = urlRepository.findByUserIdAndUrl(userId, url)
        if (existing != null) {
            throw io.hunknownn.urljarvis.adapter.`in`.web.exception.UrlDuplicateException(existing.id)
        }

        val domain = URI.create(url).host ?: throw IllegalArgumentException("Invalid URL: $url")

        val saved = urlRepository.save(
            Url(userId = userId, url = url, domain = domain)
        )

        crawlEventPublisher.publishCrawlRequested(saved.id)
        log.info("URL 등록: id={}, url={}, domain={}", saved.id, url, domain)
        return saved
    }

    override fun getUrls(userId: Long, page: Int, size: Int): Page<Url> =
        urlRepository.findByUserId(userId, PageRequest.of(page, size))

    override fun getUrl(userId: Long, urlId: Long): Url =
        urlRepository.findByIdAndUserId(urlId, userId)
            ?: throw UrlNotFoundException(urlId)

    @Transactional
    override fun deleteUrl(userId: Long, urlId: Long) {
        val url = urlRepository.findByIdAndUserId(urlId, userId)
            ?: throw UrlNotFoundException(urlId)
        urlChunkRepository.deleteByUrlId(url.id)
        urlRepository.deleteById(url.id)
        log.info("URL 삭제: id={}, url={}", url.id, url.url)
    }

    @Transactional
    override fun recrawlUrl(userId: Long, urlId: Long): Url {
        val url = urlRepository.findByIdAndUserId(urlId, userId)
            ?: throw UrlNotFoundException(urlId)
        if (url.status == CrawlStatus.CRAWLING) {
            throw io.hunknownn.urljarvis.adapter.`in`.web.exception.UrlCrawlingInProgressException(urlId)
        }
        urlChunkRepository.deleteByUrlId(url.id)
        urlRepository.updateStatus(url.id, CrawlStatus.PENDING)
        crawlEventPublisher.publishCrawlRequested(url.id)
        log.info("URL 재크롤링 요청: id={}, url={}", url.id, url.url)
        return url.copy(status = CrawlStatus.PENDING)
    }
}
