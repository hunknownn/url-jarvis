package io.hunknownn.urljarvis.application.service

import io.hunknownn.urljarvis.application.port.`in`.AuthUseCase
import io.hunknownn.urljarvis.application.port.`in`.TokenPair
import io.hunknownn.urljarvis.application.port.out.auth.OAuthClient
import io.hunknownn.urljarvis.application.port.out.persistence.UserRepository
import io.hunknownn.urljarvis.domain.user.OAuthProvider
import io.hunknownn.urljarvis.domain.user.User
import io.hunknownn.urljarvis.infrastructure.security.JwtTokenProvider
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val oAuthClient: OAuthClient,
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) : AuthUseCase {

    override fun loginWithOAuth(provider: OAuthProvider, code: String, redirectUri: String): TokenPair {
        val oAuthUserInfo = oAuthClient.getUserInfo(provider, code, redirectUri)

        val user = userRepository.findByProviderAndProviderId(oAuthUserInfo.provider, oAuthUserInfo.providerId)
            ?: userRepository.save(
                User(
                    email = oAuthUserInfo.email,
                    name = oAuthUserInfo.name,
                    provider = oAuthUserInfo.provider,
                    providerId = oAuthUserInfo.providerId
                )
            )

        return generateTokenPair(user.id)
    }

    override fun refreshToken(refreshToken: String): TokenPair {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }
        val userId = jwtTokenProvider.getUserId(refreshToken)
        userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found")

        return generateTokenPair(userId)
    }

    private fun generateTokenPair(userId: Long): TokenPair = TokenPair(
        accessToken = jwtTokenProvider.generateAccessToken(userId),
        refreshToken = jwtTokenProvider.generateRefreshToken(userId)
    )
}
