package io.hunknownn.urljarvis.application.port.`in`

import io.hunknownn.urljarvis.domain.search.SearchResult

/** 시맨틱 검색 + LLM 답변 생성 Input Port */
interface SearchUseCase {
    data class SearchAnswer(
        val answer: String,
        val sources: List<SearchResult>
    )

    /** 사용자의 전체 URL을 대상으로 검색 */
    fun searchAll(userId: Long, query: String, topK: Int = 5): SearchAnswer

    /** 특정 URL 내에서 검색 */
    fun searchByUrl(userId: Long, urlId: Long, query: String, topK: Int = 5): SearchAnswer
}
