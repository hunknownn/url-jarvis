package io.hunknownn.urljarvis.adapter.`in`.web.dto.request

import jakarta.validation.constraints.NotBlank

data class SearchRequest(
    @field:NotBlank(message = "검색어는 필수입니다")
    val query: String = "",
    val topK: Int = 5
)
