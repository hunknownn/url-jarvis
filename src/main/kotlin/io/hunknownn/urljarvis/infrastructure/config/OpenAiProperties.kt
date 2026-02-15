package io.hunknownn.urljarvis.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "url-jarvis.openai")
data class OpenAiProperties(
    val apiKey: String = "",
    val model: String = "gpt-4o-mini",
    val maxTokens: Int = 500
)
