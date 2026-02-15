package io.hunknownn.urljarvis.application.port.out.llm

/** 컨텍스트 기반 자연어 답변 생성 Output Port. 구현체: OpenAiAdapter */
interface LlmClient {
    fun generate(query: String, context: String): String
}
