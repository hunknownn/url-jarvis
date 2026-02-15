package io.hunknownn.urljarvis.adapter.out.event

import io.hunknownn.urljarvis.application.port.out.event.CrawlEventPublisher
import io.hunknownn.urljarvis.infrastructure.async.CrawlRequestedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringEventAdapter(
    private val eventPublisher: ApplicationEventPublisher
) : CrawlEventPublisher {

    override fun publishCrawlRequested(urlId: Long) {
        eventPublisher.publishEvent(CrawlRequestedEvent(urlId))
    }
}
