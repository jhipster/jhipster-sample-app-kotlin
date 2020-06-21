package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.domain.User
import io.github.jhipster.sample.repository.UserRepository
import io.github.jhipster.sample.web.rest.vm.LoginVM
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 * Integration tests for the [UserJWTController] REST controller.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = [JhipsterApp::class])
class UserJWTControllerIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testAuthorize() {
        val user = User(
            login = "user-jwt-controller",
            email = "user-jwt-controller@example.com",
            activated = true,
            password = passwordEncoder.encode("test")
        )

        userRepository.saveAndFlush(user)

        val login = LoginVM(username = "user-jwt-controller", password = "test")
        mockMvc.perform(
            post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(login))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.id_token").isString)
            .andExpect(jsonPath("\$.id_token").isNotEmpty)
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(`is`(emptyString()))))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testAuthorizeWithRememberMe() {
        val user = User(
            login = "user-jwt-controller-remember-me",
            email = "user-jwt-controller-remember-me@example.com",
            activated = true,
            password = passwordEncoder.encode("test")
        )

        userRepository.saveAndFlush(user)

        val login = LoginVM(
            username = "user-jwt-controller-remember-me",
            password = "test",
            isRememberMe = true
        )
        mockMvc.perform(
            post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(login))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.id_token").isString)
            .andExpect(jsonPath("\$.id_token").isNotEmpty)
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(`is`(emptyString()))))
    }

    @Test
    @Throws(Exception::class)
    fun testAuthorizeFails() {
        val login = LoginVM(username = "wrong-user", password = "wrong password")
        mockMvc.perform(
            post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(login))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("\$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"))
    }
}
