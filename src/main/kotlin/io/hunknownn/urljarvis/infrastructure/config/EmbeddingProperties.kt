package io.hunknownn.urljarvis.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "url-jarvis.embedding")
data class EmbeddingProperties(
    val baseUrl: String = "http://localhost:8081",
    val model: String = "intfloat/multilingual-e5-small",
    val dimensions: Int = 384
)
