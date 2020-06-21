package io.github.jhipster.sample.service

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.config.ANONYMOUS_USER
import io.github.jhipster.sample.domain.User
import io.github.jhipster.sample.repository.UserRepository
import io.github.jhipster.security.RandomUtil
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAccessor
import java.util.Optional
import kotlin.test.assertNotNull
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.auditing.AuditingHandler
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

private const val DEFAULT_LOGIN = "johndoe"
private const val DEFAULT_EMAIL = "johndoe@localhost"
private const val DEFAULT_FIRSTNAME = "john"
private const val DEFAULT_LASTNAME = "doe"
private const val DEFAULT_IMAGEURL = "http://placehold.it/50x50"
private const val DEFAULT_LANGKEY = "dummy"

/**
 * Integration tests for [UserService].
 */
@SpringBootTest(classes = [JhipsterApp::class])
@Transactional
class UserServiceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var auditingHandler: AuditingHandler

    @Mock
    private lateinit var dateTimeProvider: DateTimeProvider

    private lateinit var user: User

    @BeforeEach
    fun init() {
        user = User(
            login = DEFAULT_LOGIN,
            password = RandomStringUtils.random(60),
            activated = true,
            email = DEFAULT_EMAIL,
            firstName = DEFAULT_FIRSTNAME,
            lastName = DEFAULT_LASTNAME,
            imageUrl = DEFAULT_IMAGEURL,
            langKey = DEFAULT_LANGKEY
        )

        `when`<Optional<TemporalAccessor>>(dateTimeProvider.now).thenReturn(Optional.of(LocalDateTime.now()))
        auditingHandler.setDateTimeProvider(dateTimeProvider)
    }

    @Test
    @Transactional
    fun assertThatUserMustExistToResetPassword() {
        userRepository.saveAndFlush(user)
        var maybeUser = userService.requestPasswordReset("invalid.login@localhost")
        assertThat(maybeUser).isNotPresent

        maybeUser = userService.requestPasswordReset(user.email!!)
        assertThat(maybeUser).isPresent
        assertThat(maybeUser.orElse(null).email).isEqualTo(user.email)
        assertThat(maybeUser.orElse(null).resetDate).isNotNull()
        assertThat(maybeUser.orElse(null).resetKey).isNotNull()
    }

    @Test
    @Transactional
    fun assertThatOnlyActivatedUserCanRequestPasswordReset() {
        user.activated = false
        userRepository.saveAndFlush(user)

        val maybeUser = userService.requestPasswordReset(user.login!!)
        assertThat(maybeUser).isNotPresent
        userRepository.delete(user)
    }

    @Test
    @Transactional
    fun assertThatResetKeyMustNotBeOlderThan24Hours() {
        val daysAgo = Instant.now().minus(25, ChronoUnit.HOURS)
        val resetKey = RandomUtil.generateResetKey()
        user.activated = true
        user.resetDate = daysAgo
        user.resetKey = resetKey
        userRepository.saveAndFlush(user)

        val maybeUser = userService.completePasswordReset("johndoe2", user.resetKey!!)
        assertThat(maybeUser).isNotPresent
        userRepository.delete(user)
    }

    @Test
    @Transactional
    fun assertThatResetKeyMustBeValid() {
        val daysAgo = Instant.now().minus(25, ChronoUnit.HOURS)
        user.activated = true
        user.resetDate = daysAgo
        user.resetKey = "1234"
        userRepository.saveAndFlush(user)

        val maybeUser = userService.completePasswordReset("johndoe2", user.resetKey!!)
        assertThat(maybeUser).isNotPresent
        userRepository.delete(user)
    }

    @Test
    @Transactional
    fun assertThatUserCanResetPassword() {
        val oldPassword = user.password
        val daysAgo = Instant.now().minus(2, ChronoUnit.HOURS)
        val resetKey = RandomUtil.generateResetKey()
        user.activated = true
        user.resetDate = daysAgo
        user.resetKey = resetKey
        userRepository.saveAndFlush(user)

        val maybeUser = userService.completePasswordReset("johndoe2", user.resetKey!!)
        assertThat(maybeUser).isPresent
        assertThat(maybeUser.orElse(null).resetDate).isNull()
        assertThat(maybeUser.orElse(null).resetKey).isNull()
        assertThat(maybeUser.orElse(null).password).isNotEqualTo(oldPassword)

        userRepository.delete(user)
    }

    @Test
    @Transactional
    fun assertThatNotActivatedUsersWithNotNullActivationKeyCreatedBefore3DaysAreDeleted() {
        val now = Instant.now()
        `when`<Optional<TemporalAccessor>>(dateTimeProvider.now).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)))
        user.activated = false
        user.activationKey = RandomStringUtils.random(20)
        val dbUser = userRepository.saveAndFlush(user)
        assertNotNull(dbUser)
        dbUser.createdDate = now.minus(4, ChronoUnit.DAYS)
        userRepository.saveAndFlush(user)
        val threeDaysAgo = now.minus(3, ChronoUnit.DAYS)
        var users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
            threeDaysAgo
        )
        assertThat(users).isNotEmpty
        userService.removeNotActivatedUsers()
        users =
            userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                threeDaysAgo
            )
        assertThat(users).isEmpty()
    }

    @Test
    @Transactional
    fun assertThatNotActivatedUsersWithNullActivationKeyCreatedBefore3DaysAreNotDeleted() {
        val now = Instant.now()
        `when`(dateTimeProvider.getNow()).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)))
        user.activated = false
        val dbUser = userRepository.saveAndFlush(user)
        dbUser.createdDate = now.minus(4, ChronoUnit.DAYS)
        userRepository.saveAndFlush(user)
        val threeDaysAgo = now.minus(3, ChronoUnit.DAYS)
        val users =
            userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                threeDaysAgo
            )
        assertThat(users).isEmpty()
        userService.removeNotActivatedUsers()
        val maybeDbUser = userRepository.findById(dbUser.id)
        assertThat(maybeDbUser).contains(dbUser)
    }

    @Test
    @Transactional
    fun assertThatAnonymousUserIsNotGet() {
        user.login = ANONYMOUS_USER
        if (!userRepository.findOneByLogin(ANONYMOUS_USER).isPresent) {
            userRepository.saveAndFlush(user)
        }
        val pageable = PageRequest.of(0, userRepository.count().toInt())
        val allManagedUsers = userService.getAllManagedUsers(pageable)
        assertNotNull(allManagedUsers)
        assertThat(allManagedUsers.content.stream()
            .noneMatch { user -> ANONYMOUS_USER == user.login })
            .isTrue()
    }
}
