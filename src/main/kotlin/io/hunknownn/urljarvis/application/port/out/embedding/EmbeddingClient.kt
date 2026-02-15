package io.hunknownn.urljarvis.application.port.out.embedding

/** 텍스트 → 벡터 임베딩 변환 Output Port. 구현체: EmbeddingRestAdapter */
interface EmbeddingClient {
    fun embed(text: String): FloatArray
    fun embedBatch(texts: List<String>, batchIndex: Int = 0, totalBatches: Int = 0): List<FloatArray>
}
