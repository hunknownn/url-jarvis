package io.hunknownn.urljarvis.application.port.`in`

import io.hunknownn.urljarvis.domain.url.Url
import org.springframework.data.domain.Page

interface ManageUrlUseCase {
    fun getUrls(userId: Long, page: Int, size: Int): Page<Url>
    fun getUrl(userId: Long, urlId: Long): Url
    fun deleteUrl(userId: Long, urlId: Long)
    fun recrawlUrl(userId: Long, urlId: Long): Url
}
