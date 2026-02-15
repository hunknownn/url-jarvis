package io.hunknownn.urljarvis.application.port.out.auth

import io.hunknownn.urljarvis.domain.user.OAuthProvider

data class OAuthUserInfo(
    val provider: OAuthProvider,
    val providerId: String,
    val email: String,
    val name: String
)

interface OAuthClient {
    fun getUserInfo(provider: OAuthProvider, code: String, redirectUri: String): OAuthUserInfo
}
