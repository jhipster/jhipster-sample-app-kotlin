package io.github.jhipster.sample.security.jwt

import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.sample.security.ANONYMOUS
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.Date
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.util.ReflectionTestUtils

private const val ONE_MINUTE: Long = 60000

class TokenProviderTest {

    private lateinit var key: Key
    private lateinit var tokenProvider: TokenProvider

    @BeforeEach
    fun setup() {
        tokenProvider = TokenProvider(JHipsterProperties())
        key = Keys.hmacShaKeyFor(
            Decoders.BASE64
                .decode("fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
        )

        ReflectionTestUtils.setField(tokenProvider, "key", key)
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", ONE_MINUTE)
    }

    @Test
    fun testReturnFalseWhenJWThasInvalidSignature() {
        val isTokenValid = tokenProvider.validateToken(createTokenWithDifferentSignature())

        assertThat(isTokenValid).isEqualTo(false)
    }

    @Test
    fun testReturnFalseWhenJWTisMalformed() {
        val authentication = createAuthentication()
        val token = tokenProvider.createToken(authentication, false)
        val invalidToken = token.substring(1)
        val isTokenValid = tokenProvider.validateToken(invalidToken)

        assertThat(isTokenValid).isEqualTo(false)
    }

    @Test
    fun testReturnFalseWhenJWTisExpired() {
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -ONE_MINUTE)

        val authentication = createAuthentication()
        val token = tokenProvider.createToken(authentication, false)

        val isTokenValid = tokenProvider.validateToken(token)

        assertThat(isTokenValid).isEqualTo(false)
    }

    @Test
    fun testReturnFalseWhenJWTisUnsupported() {
        val unsupportedToken = createUnsupportedToken()

        val isTokenValid = tokenProvider.validateToken(unsupportedToken)

        assertThat(isTokenValid).isEqualTo(false)
    }

    @Test
    fun testReturnFalseWhenJWTisInvalid() {
        val isTokenValid = tokenProvider.validateToken("")

        assertThat(isTokenValid).isEqualTo(false)
    }

    private fun createAuthentication(): Authentication {
        val authorities = mutableListOf(SimpleGrantedAuthority(ANONYMOUS))
        return UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities)
    }

    private fun createUnsupportedToken() =
        Jwts.builder()
            .setPayload("payload")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()

    private fun createTokenWithDifferentSignature(): String {
        val otherKey = Keys.hmacShaKeyFor(
            Decoders.BASE64
                .decode("Xfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
        )

        return Jwts.builder()
            .setSubject("anonymous")
            .signWith(otherKey, SignatureAlgorithm.HS512)
            .setExpiration(Date(Date().time + ONE_MINUTE))
            .compact()
    }
}
