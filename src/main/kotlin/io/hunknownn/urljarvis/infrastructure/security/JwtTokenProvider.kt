package io.hunknownn.urljarvis.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성/검증.
 * - Access Token (1h): API 요청 인증
 * - Refresh Token (14d): Access Token 갱신용
 * - Payload의 subject에 userId를 저장한다.
 */
@Component
class JwtTokenProvider(
    @Value("\${url-jarvis.jwt.secret}") private val secret: String,
    @Value("\${url-jarvis.jwt.access-token-expiry}") private val accessTokenExpiry: Long,
    @Value("\${url-jarvis.jwt.refresh-token-expiry}") private val refreshTokenExpiry: Long
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateAccessToken(userId: Long): String =
        generateToken(userId, accessTokenExpiry * 1000)

    fun generateRefreshToken(userId: Long): String =
        generateToken(userId, refreshTokenExpiry * 1000)

    fun validateToken(token: String): Boolean =
        try {
            parseClaims(token)
            true
        } catch (_: Exception) {
            false
        }

    fun getUserId(token: String): Long =
        parseClaims(token).subject.toLong()

    private fun generateToken(userId: Long, expirationMs: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(Date(now.time + expirationMs))
            .signWith(key)
            .compact()
    }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
