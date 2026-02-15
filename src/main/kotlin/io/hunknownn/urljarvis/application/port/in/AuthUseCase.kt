package io.hunknownn.urljarvis.application.port.`in`

import io.hunknownn.urljarvis.domain.user.OAuthProvider

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)

interface AuthUseCase {
    fun loginWithOAuth(provider: OAuthProvider, code: String, redirectUri: String): TokenPair
    fun refreshToken(refreshToken: String): TokenPair
}
