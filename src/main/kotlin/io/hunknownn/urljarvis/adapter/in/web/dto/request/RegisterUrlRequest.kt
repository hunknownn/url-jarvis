package io.hunknownn.urljarvis.adapter.`in`.web.dto.request

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class RegisterUrlRequest(
    @field:NotBlank @field:URL val url: String
)
