package io.hunknownn.urljarvis.adapter.out.embedding

import io.hunknownn.urljarvis.application.port.out.embedding.EmbeddingClient
import io.hunknownn.urljarvis.infrastructure.config.EmbeddingProperties
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import kotlin.system.measureTimeMillis

/**
 * 외부 임베딩 서버(intfloat/multilingual-e5-small)와 REST 통신하는 어댑터.
 *
 * 호출자(CrawlPipelineService, SearchService)가 e5 prefix("query: ", "passage: ")를
 * 직접 추가한 텍스트를 전달해야 한다.
 */
@Component
class EmbeddingRestAdapter(
    private val webClient: WebClient,
    private val embeddingProperties: EmbeddingProperties
) : EmbeddingClient {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun embed(text: String): FloatArray =
        embedBatch(listOf(text)).first()

    @Suppress("UNCHECKED_CAST")
    override fun embedBatch(texts: List<String>, batchIndex: Int, totalBatches: Int): List<FloatArray> {
        val batchLabel = if (totalBatches > 0) "[배치 $batchIndex/$totalBatches] " else ""
        log.info("임베딩 요청: {}{}건 (서버: {})", batchLabel, texts.size, embeddingProperties.baseUrl)

        val response: Map<*, *>
        val elapsed = measureTimeMillis {
            response = webClient.post()
                .uri("${embeddingProperties.baseUrl}/embed")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                    mapOf(
                        "texts" to texts,
                        "model" to embeddingProperties.model
                    )
                )
                .retrieve()
                .bodyToMono(Map::class.java)
                .block() ?: throw RuntimeException("Embedding server returned empty response")
        }

        val embeddings = response["embeddings"] as? List<List<Number>>
            ?: throw RuntimeException("No embeddings in response")

        log.info("임베딩 완료: {}{}건 ({}차원) - {}ms (건당 {}ms)", batchLabel, embeddings.size, embeddings.firstOrNull()?.size ?: 0, elapsed, elapsed / texts.size)

        return embeddings.map { vector ->
            FloatArray(vector.size) { i -> vector[i].toFloat() }
        }
    }
}
