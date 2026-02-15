package io.hunknownn.urljarvis.adapter.out.crawling

import io.hunknownn.urljarvis.application.port.out.crawling.CrawlResult
import io.hunknownn.urljarvis.application.port.out.crawling.WebCrawler
import io.hunknownn.urljarvis.infrastructure.config.FirecrawlProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FirecrawlAdapter(
    private val webClient: WebClient,
    private val firecrawlProperties: FirecrawlProperties
) : WebCrawler {

    @Suppress("UNCHECKED_CAST")
    override fun crawl(url: String): CrawlResult {
        val response = webClient.post()
            .uri("${firecrawlProperties.baseUrl}/scrape")
            .header("Authorization", "Bearer ${firecrawlProperties.apiKey}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("url" to url, "formats" to listOf("markdown")))
            .retrieve()
            .bodyToMono(Map::class.java)
            .block() ?: throw RuntimeException("Firecrawl returned empty response for: $url")

        val success = response["success"] as? Boolean ?: false
        if (!success) {
            throw RuntimeException("Firecrawl failed for URL: $url")
        }

        val data = response["data"] as? Map<String, Any>
            ?: throw RuntimeException("No data in Firecrawl response for: $url")

        val metadata = data["metadata"] as? Map<String, Any> ?: emptyMap()

        return CrawlResult(
            markdown = data["markdown"] as? String ?: "",
            title = metadata["title"] as? String,
            description = metadata["description"] as? String
        )
    }
}
