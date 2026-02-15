package io.hunknownn.urljarvis.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "url-jarvis.chunking")
data class ChunkingProperties(
    val chunkSize: Int = 500,
    val chunkOverlap: Int = 100
)
