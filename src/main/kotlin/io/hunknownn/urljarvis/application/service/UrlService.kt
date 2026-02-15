package io.hunknownn.urljarvis.application.service

import io.hunknownn.urljarvis.adapter.`in`.web.exception.UrlNotFoundException
import io.hunknownn.urljarvis.application.port.`in`.ManageUrlUseCase
import io.hunknownn.urljarvis.application.port.`in`.RegisterUrlUseCase
import io.hunknownn.urljarvis.application.port.out.event.CrawlEventPublisher
import io.hunknownn.urljarvis.application.port.out.persistence.UrlChunkRepository
import io.hunknownn.urljarvis.application.port.out.persistence.UrlRepository
import io.hunknownn.urljarvis.domain.url.CrawlStatus
import io.hunknownn.urljarvis.domain.url.Url
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

    @Transactional
    override fun register(userId: Long, url: String): Url {
        val domain = URI.create(url).host ?: throw IllegalArgumentException("Invalid URL: $url")

        val saved = urlRepository.save(
            Url(userId = userId, url = url, domain = domain)
        )

        crawlEventPublisher.publishCrawlRequested(saved.id)
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
    }

    @Transactional
    override fun recrawlUrl(userId: Long, urlId: Long): Url {
        val url = urlRepository.findByIdAndUserId(urlId, userId)
            ?: throw UrlNotFoundException(urlId)
        urlChunkRepository.deleteByUrlId(url.id)
        urlRepository.updateStatus(url.id, CrawlStatus.PENDING)
        crawlEventPublisher.publishCrawlRequested(url.id)
        return url.copy(status = CrawlStatus.PENDING)
    }
}
