package io.github.jhipster.sample.security

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.domain.User
import io.github.jhipster.sample.repository.UserRepository
import java.util.Locale
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.transaction.annotation.Transactional

private const val USER_ONE_LOGIN = "test-user-one"
private const val USER_ONE_EMAIL = "test-user-one@localhost"
private const val USER_TWO_LOGIN = "test-user-two"
private const val USER_TWO_EMAIL = "test-user-two@localhost"
private const val USER_THREE_LOGIN = "test-user-three"
private const val USER_THREE_EMAIL = "test-user-three@localhost"

/**
 * Integration tests for [DomainUserDetailsService].
 */
@SpringBootTest(classes = [JhipsterApp::class])
@Transactional
class DomainUserDetailsServiceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var domainUserDetailsService: UserDetailsService

    @BeforeEach
    fun init() {
        val userOne = User(
            login = USER_ONE_LOGIN,
            password = RandomStringUtils.random(60),
            activated = true,
            email = USER_ONE_EMAIL,
            firstName = "userOne",
            lastName = "doe",
            langKey = "en"
        )
        userRepository.save(userOne)

        val userTwo = User(
            login = USER_TWO_LOGIN,
            password = RandomStringUtils.random(60),
            activated = true,
            email = USER_TWO_EMAIL,
            firstName = "userTwo",
            lastName = "doe",
            langKey = "en"
        )
        userRepository.save(userTwo)

        val userThree = User(
            login = USER_THREE_LOGIN,
            password = RandomStringUtils.random(60),
            activated = false,
            email = USER_THREE_EMAIL,
            firstName = "userThree",
            lastName = "doe",
            langKey = "en"
        )
        userRepository.save(userThree)
    }

    @Test
    fun assertThatUserCanBeFoundByLogin() {
        val userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN)
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_ONE_LOGIN)
    }

    @Test
    fun assertThatUserCanBeFoundByLoginIgnoreCase() {
        val userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH))
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_ONE_LOGIN)
    }

    @Test
    fun assertThatUserCanBeFoundByEmail() {
        val userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL)
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_TWO_LOGIN)
    }

    @Test
    fun assertThatUserCanBeFoundByEmailIgnoreCase() {
        val userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL.toUpperCase(Locale.ENGLISH))
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_TWO_LOGIN)
    }

    @Test
    fun assertThatEmailIsPrioritizedOverLogin() {
        val userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_EMAIL)
        assertThat(userDetails).isNotNull
        assertThat(userDetails.username).isEqualTo(USER_ONE_LOGIN)
    }

    @Test
    fun assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
        assertThatExceptionOfType(UserNotActivatedException::class.java).isThrownBy {
            domainUserDetailsService.loadUserByUsername(USER_THREE_LOGIN)
        }
    }
}
