package io.hunknownn.urljarvis.adapter.`in`.web

import io.hunknownn.urljarvis.adapter.`in`.web.dto.request.RegisterUrlRequest
import io.hunknownn.urljarvis.adapter.`in`.web.dto.response.ApiResponse
import io.hunknownn.urljarvis.adapter.`in`.web.dto.response.UrlResponse
import io.hunknownn.urljarvis.application.port.`in`.ManageUrlUseCase
import io.hunknownn.urljarvis.application.port.`in`.RegisterUrlUseCase
import io.hunknownn.urljarvis.infrastructure.security.AuthenticatedUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/urls")
class UrlController(
    private val registerUrlUseCase: RegisterUrlUseCase,
    private val manageUrlUseCase: ManageUrlUseCase
) {
    @PostMapping
    fun register(
        @RequestBody @Valid request: RegisterUrlRequest
    ): ResponseEntity<ApiResponse<UrlResponse>> {
        val url = registerUrlUseCase.register(AuthenticatedUser.getId(), request.url)
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(ApiResponse.ok(UrlResponse.from(url)))
    }

    @GetMapping
    fun getUrls(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val urlPage = manageUrlUseCase.getUrls(AuthenticatedUser.getId(), page, size)
        val response = mapOf(
            "content" to urlPage.content.map { UrlResponse.from(it) },
            "page" to urlPage.number,
            "size" to urlPage.size,
            "totalElements" to urlPage.totalElements,
            "totalPages" to urlPage.totalPages
        )
        return ResponseEntity.ok(ApiResponse.ok(response))
    }

    @GetMapping("/{id}")
    fun getUrl(@PathVariable id: Long): ResponseEntity<ApiResponse<UrlResponse>> {
        val url = manageUrlUseCase.getUrl(AuthenticatedUser.getId(), id)
        return ResponseEntity.ok(ApiResponse.ok(UrlResponse.from(url)))
    }

    @DeleteMapping("/{id}")
    fun deleteUrl(@PathVariable id: Long): ResponseEntity<Void> {
        manageUrlUseCase.deleteUrl(AuthenticatedUser.getId(), id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/recrawl")
    fun recrawlUrl(@PathVariable id: Long): ResponseEntity<ApiResponse<UrlResponse>> {
        val url = manageUrlUseCase.recrawlUrl(AuthenticatedUser.getId(), id)
        return ResponseEntity.accepted().body(ApiResponse.ok(UrlResponse.from(url)))
    }
}
