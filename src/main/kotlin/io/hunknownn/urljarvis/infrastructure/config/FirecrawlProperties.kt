package io.hunknownn.urljarvis.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "url-jarvis.firecrawl")
data class FirecrawlProperties(
    val apiKey: String = "",
    val baseUrl: String = "https://api.firecrawl.dev/v1"
)
