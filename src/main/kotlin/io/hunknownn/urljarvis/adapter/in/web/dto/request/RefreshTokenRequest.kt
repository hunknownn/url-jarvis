package io.hunknownn.urljarvis.adapter.`in`.web.dto.request

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank val refreshToken: String
)
