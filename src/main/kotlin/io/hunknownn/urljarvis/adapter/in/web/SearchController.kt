package io.hunknownn.urljarvis.adapter.`in`.web

import io.hunknownn.urljarvis.adapter.`in`.web.dto.request.SearchRequest
import io.hunknownn.urljarvis.adapter.`in`.web.dto.response.ApiResponse
import io.hunknownn.urljarvis.adapter.`in`.web.dto.response.SearchResponse
import io.hunknownn.urljarvis.application.port.`in`.SearchUseCase
import io.hunknownn.urljarvis.infrastructure.security.AuthenticatedUser
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SearchController(
    private val searchUseCase: SearchUseCase
) {
    /** 사용자의 전체 URL을 대상으로 시맨틱 검색 */
    @PostMapping("/search")
    fun searchAll(
        @RequestBody @Valid request: SearchRequest
    ): ResponseEntity<ApiResponse<SearchResponse>> {
        val result = searchUseCase.searchAll(
            userId = AuthenticatedUser.getId(),
            query = request.query,
            topK = request.topK
        )
        return ResponseEntity.ok(ApiResponse.ok(SearchResponse.from(result)))
    }

    /** 특정 URL 내에서 시맨틱 검색 */
    @PostMapping("/urls/{urlId}/search")
    fun searchByUrl(
        @PathVariable urlId: Long,
        @RequestBody @Valid request: SearchRequest
    ): ResponseEntity<ApiResponse<SearchResponse>> {
        val result = searchUseCase.searchByUrl(
            userId = AuthenticatedUser.getId(),
            urlId = urlId,
            query = request.query,
            topK = request.topK
        )
        return ResponseEntity.ok(ApiResponse.ok(SearchResponse.from(result)))
    }
}
