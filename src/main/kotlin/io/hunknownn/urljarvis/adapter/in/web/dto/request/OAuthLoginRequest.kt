package io.hunknownn.urljarvis.adapter.`in`.web.dto.request

import jakarta.validation.constraints.NotBlank

data class OAuthLoginRequest(
    @field:NotBlank val code: String,
    @field:NotBlank val redirectUri: String
)
