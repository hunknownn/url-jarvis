package io.hunknownn.urljarvis.application.port.`in`

import io.hunknownn.urljarvis.domain.url.Url

interface RegisterUrlUseCase {
    fun register(userId: Long, url: String): Url
}
