package io.hunknownn.urljarvis.adapter.`in`.web.exception

import io.hunknownn.urljarvis.adapter.`in`.web.dto.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class UrlNotFoundException(val urlId: Long) : RuntimeException("URL not found: $urlId")
class UrlDuplicateException(val existingUrlId: Long) : RuntimeException("이미 등록된 URL입니다 (id: $existingUrlId)")
class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFound(e: UrlNotFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.message ?: "URL not found"))

    @ExceptionHandler(UrlDuplicateException::class)
    fun handleUrlDuplicate(e: UrlDuplicateException): ResponseEntity<ApiResponse<Map<String, Long>>> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse(success = false, data = mapOf("existingUrlId" to e.existingUrlId), error = e.message))

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(e.message ?: "Unauthorized"))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.message ?: "Bad request"))

    @ExceptionHandler(Exception::class)
    fun handleGeneral(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Internal server error"))
    }
}
