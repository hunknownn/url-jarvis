package io.hunknownn.urljarvis.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "url-jarvis.crawl.debug")
data class CrawlDebugProperties(
    val saveMarkdown: Boolean = false,
    val outputDir: String = "crawl-debug"
)
