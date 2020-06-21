package io.github.jhipster.sample.web.rest

import io.github.jhipster.sample.JhipsterApp
import io.github.jhipster.sample.config.DEFAULT_LANGUAGE
import io.github.jhipster.sample.domain.User
import io.github.jhipster.sample.repository.AuthorityRepository
import io.github.jhipster.sample.repository.UserRepository
import io.github.jhipster.sample.security.ADMIN
import io.github.jhipster.sample.security.USER
import io.github.jhipster.sample.service.UserService
import io.github.jhipster.sample.service.dto.PasswordChangeDTO
import io.github.jhipster.sample.service.dto.UserDTO
import io.github.jhipster.sample.web.rest.vm.KeyAndPasswordVM
import io.github.jhipster.sample.web.rest.vm.ManagedUserVM
import java.time.Instant
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 * Integrations tests for the [AccountResource] REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(value = TEST_USER_LOGIN)
@SpringBootTest(classes = [JhipsterApp::class])
class AccountResourceIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var restAccountMockMvc: MockMvc

    @Test
    @WithUnauthenticatedMockUser
    @Throws(Exception::class)
    fun testNonAuthenticatedUser() {
        restAccountMockMvc.perform(
            get("/api/authenticate")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }

    @Test
    @Throws(Exception::class)
    fun testAuthenticatedUser() {
        restAccountMockMvc.perform(get("/api/authenticate")
            .with { request ->
                request.remoteUser = TEST_USER_LOGIN
                request
            }
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().string(TEST_USER_LOGIN))
    }

    @Test
    @Throws(Exception::class)
    fun testGetExistingAccount() {

        val authorities = mutableSetOf(ADMIN)

        val user = UserDTO(
            login = TEST_USER_LOGIN,
            firstName = "john",
            lastName = "doe",
            email = "john.doe@jhipster.com",
            imageUrl = "http://placehold.it/50x50",
            langKey = "en",
            authorities = authorities
        )
        userService.createUser(user)

        restAccountMockMvc.perform(
            get("/api/account")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("\$.login").value(TEST_USER_LOGIN))
            .andExpect(jsonPath("\$.firstName").value("john"))
            .andExpect(jsonPath("\$.lastName").value("doe"))
            .andExpect(jsonPath("\$.email").value("john.doe@jhipster.com"))
            .andExpect(jsonPath("\$.imageUrl").value("http://placehold.it/50x50"))
            .andExpect(jsonPath("\$.langKey").value("en"))
            .andExpect(jsonPath("\$.authorities").value(ADMIN))
    }

    @Test
    @Throws(Exception::class)
    fun testGetUnknownAccount() {
        restAccountMockMvc.perform(get("/api/account")
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isInternalServerError)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterValid() {
        val validUser = ManagedUserVM().apply {
            login = "test-register-valid"
            password = "password"
            firstName = "Alice"
            lastName = "Test"
            email = "test-register-valid@example.com"
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }
        assertThat(userRepository.findOneByLogin("test-register-valid").isPresent).isFalse()

        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(validUser))
        )
            .andExpect(status().isCreated)

        assertThat(userRepository.findOneByLogin("test-register-valid").isPresent).isTrue()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterInvalidLogin() {
        val invalidUser = ManagedUserVM().apply {
            login = "funky-log(n" // <-- invalid
            password = "password"
            firstName = "Funky"
            lastName = "One"
            email = "funky@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }

        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invalidUser))
        )
            .andExpect(status().isBadRequest)

        val user = userRepository.findOneByEmailIgnoreCase("funky@example.com")
        assertThat(user.isPresent).isFalse()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterInvalidEmail() {
        val invalidUser = ManagedUserVM().apply {
            login = "bob"
            password = "password"
            firstName = "Bob"
            lastName = "Green"
            email = "invalid" // <-- invalid
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }

        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invalidUser))
        )
            .andExpect(status().isBadRequest)

        val user = userRepository.findOneByLogin("bob")
        assertThat(user.isPresent).isFalse()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterInvalidPassword() {
        val invalidUser = ManagedUserVM().apply {
            login = "bob"
            password = "123" // password with only 3 digits
            firstName = "Bob"
            lastName = "Green"
            email = "bob@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }

        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invalidUser))
        )
            .andExpect(status().isBadRequest)

        val user = userRepository.findOneByLogin("bob")
        assertThat(user.isPresent).isFalse()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterNullPassword() {
        val invalidUser = ManagedUserVM().apply {
            login = "bob"
            password = null // invalid null password
            firstName = "Bob"
            lastName = "Green"
            email = "bob@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }

        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invalidUser))
        )
            .andExpect(status().isBadRequest)

        val user = userRepository.findOneByLogin("bob")
        assertThat(user.isPresent).isFalse()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterDuplicateLogin() {
        // First registration
        val firstUser = ManagedUserVM().apply {
            login = "alice"
            password = "password"
            firstName = "Alice"
            lastName = "Something"
            email = "alice@example.com"
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }

        // Duplicate login, different email
        val secondUser = ManagedUserVM().apply {
            login = firstUser.login
            password = firstUser.password
            firstName = firstUser.firstName
            lastName = firstUser.lastName
            email = "alice2@example.com"
            imageUrl = firstUser.imageUrl
            langKey = firstUser.langKey
            createdBy = firstUser.createdBy
            createdDate = firstUser.createdDate
            lastModifiedBy = firstUser.lastModifiedBy
            lastModifiedDate = firstUser.lastModifiedDate
            authorities = firstUser.authorities?.toMutableSet()
        }

        // First user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(firstUser))
        )
            .andExpect(status().isCreated)

        // Second (non activated) user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(secondUser))
        )
            .andExpect(status().isCreated)

        val testUser = userRepository.findOneByEmailIgnoreCase("alice2@example.com")
        assertThat(testUser.isPresent).isTrue()
        testUser.get().activated = true
        userRepository.save(testUser.get())

        // Second (already activated) user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(secondUser))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterDuplicateEmail() {
        // First user
        val firstUser = ManagedUserVM().apply {
            login = "test-register-duplicate-email"
            password = "password"
            firstName = "Alice"
            lastName = "Test"
            email = "test-register-duplicate-email@example.com"
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(USER)
        }

        // Register first user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(firstUser))
        )
            .andExpect(status().isCreated)

        val testUser1 = userRepository.findOneByLogin("test-register-duplicate-email")
        assertThat(testUser1.isPresent).isTrue()

        // Duplicate email, different login
        val secondUser = ManagedUserVM().apply {
            login = "test-register-duplicate-email-2"
            password = firstUser.password
            firstName = firstUser.firstName
            lastName = firstUser.lastName
            email = firstUser.email
            imageUrl = firstUser.imageUrl
            langKey = firstUser.langKey
            authorities = firstUser.authorities?.toMutableSet()
        }

        // Register second (non activated) user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(secondUser))
        )
            .andExpect(status().isCreated)

        val testUser2 = userRepository.findOneByLogin("test-register-duplicate-email")
        assertThat(testUser2.isPresent).isFalse()

        val testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2")
        assertThat(testUser3.isPresent).isTrue()

        // Duplicate email - with uppercase email address
        val userWithUpperCaseEmail = ManagedUserVM().apply {
            id = firstUser.id
            login = "test-register-duplicate-email-3"
            password = firstUser.password
            firstName = firstUser.firstName
            lastName = firstUser.lastName
            email = "TEST-register-duplicate-email@example.com"
            imageUrl = firstUser.imageUrl
            langKey = firstUser.langKey
            authorities = firstUser.authorities?.toMutableSet()
        }

        // Register third (not activated) user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(userWithUpperCaseEmail))
        )
            .andExpect(status().isCreated)

        val testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3")
        assertThat(testUser4.isPresent).isTrue()
        assertThat(testUser4.get().email).isEqualTo("test-register-duplicate-email@example.com")

        testUser4.get().activated = true
        userService.updateUser((UserDTO(testUser4.get())))

        // Register 4th (already activated) user
        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(secondUser))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRegisterAdminIsIgnored() {
        val validUser = ManagedUserVM().apply {
            login = "badguy"
            password = "password"
            firstName = "Bad"
            lastName = "Guy"
            email = "badguy@example.com"
            activated = true
            imageUrl = "http://placehold.it/50x50"
            langKey = DEFAULT_LANGUAGE
            authorities = setOf(ADMIN)
        }

        restAccountMockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(validUser))
        )
            .andExpect(status().isCreated)

        val userDup = userRepository.findOneWithAuthoritiesByLogin("badguy")
        assertThat(userDup.isPresent).isTrue()
        assertThat(userDup.get().authorities).hasSize(1)
            .containsExactly(authorityRepository.findById(USER).get())
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testActivateAccount() {
        val activationKey = "some activation key"
        var user = User(
            login = "activate-account",
            email = "activate-account@example.com",
            password = RandomStringUtils.random(60),
            activated = false,
            activationKey = activationKey
        )

        userRepository.saveAndFlush(user)

        restAccountMockMvc.perform(get("/api/activate?key={activationKey}", activationKey))
            .andExpect(status().isOk)

        user = userRepository.findOneByLogin(user.login!!).orElse(null)
        assertThat(user.activated).isTrue()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testActivateAccountWithWrongKey() {
        restAccountMockMvc.perform(get("/api/activate?key=wrongActivationKey"))
            .andExpect(status().isInternalServerError)
    }

    @Test
    @Transactional
    @WithMockUser("save-account")
    @Throws(Exception::class)
    fun testSaveAccount() {
        val user = User(
            login = "save-account",
            email = "save-account@example.com",
            password = RandomStringUtils.random(60),
            activated = true
        )

        userRepository.saveAndFlush(user)

        val userDTO = UserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "save-account@example.com",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = setOf(ADMIN)
        )

        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(userDTO))
        )
            .andExpect(status().isOk)

        val updatedUser = userRepository.findOneWithAuthoritiesByLogin(user?.login!!).orElse(null)
        assertThat(updatedUser?.firstName).isEqualTo(userDTO.firstName)
        assertThat(updatedUser?.lastName).isEqualTo(userDTO.lastName)
        assertThat(updatedUser?.email).isEqualTo(userDTO.email)
        assertThat(updatedUser?.langKey).isEqualTo(userDTO.langKey)
        assertThat(updatedUser?.password).isEqualTo(user.password)
        assertThat(updatedUser?.imageUrl).isEqualTo(userDTO.imageUrl)
        assertThat(updatedUser?.activated).isEqualTo(true)
        assertThat(updatedUser?.authorities).isEmpty()
    }

    @Test
    @Transactional
    @WithMockUser("save-invalid-email")
    @Throws(Exception::class)
    fun testSaveInvalidEmail() {
        val user = User(
            login = "save-invalid-email",
            email = "save-invalid-email@example.com",
            password = RandomStringUtils.random(60),
            activated = true
        )

        userRepository.saveAndFlush(user)

        val userDTO = UserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "invalid email",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = setOf(ADMIN)
        )

        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(userDTO))
        )
            .andExpect(status().isBadRequest)

        assertThat(userRepository.findOneByEmailIgnoreCase("invalid email")).isNotPresent
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email")
    @Throws(Exception::class)
    fun testSaveExistingEmail() {
        val user = User(
            login = "save-existing-email",
            email = "save-existing-email@example.com",
            password = RandomStringUtils.random(60),
            activated = true
        )

        userRepository.saveAndFlush(user)

        val anotherUser = User(
            login = "save-existing-email2",
            email = "save-existing-email2@example.com",
            password = RandomStringUtils.random(60),
            activated = true
        )

        userRepository.saveAndFlush(anotherUser)

        val userDTO = UserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "save-existing-email2@example.com",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = setOf(ADMIN)
        )

        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(userDTO))
        )
            .andExpect(status().isBadRequest)

        val updatedUser = userRepository.findOneByLogin("save-existing-email").orElse(null)
        assertThat(updatedUser.email).isEqualTo("save-existing-email@example.com")
    }

    @Test
    @Transactional
    @WithMockUser("save-existing-email-and-login")
@Throws(Exception::class) fun testSaveExistingEmailAndLogin() {
        val user = User(
            login = "save-existing-email-and-login",
            email = "save-existing-email-and-login@example.com",
            password = RandomStringUtils.random(60),
            activated = true
        )

        userRepository.saveAndFlush(user)

        val userDTO = UserDTO(
            login = "not-used",
            firstName = "firstname",
            lastName = "lastname",
            email = "save-existing-email-and-login@example.com",
            activated = false,
            imageUrl = "http://placehold.it/50x50",
            langKey = DEFAULT_LANGUAGE,
            authorities = setOf(ADMIN)
        )
        // Mark here....
        restAccountMockMvc.perform(
            post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(userDTO))
        )
            .andExpect(status().isOk)

        val updatedUser = userRepository.findOneByLogin("save-existing-email-and-login").orElse(null)
        assertThat(updatedUser.email).isEqualTo("save-existing-email-and-login@example.com")
    }

    @Test
    @Transactional
    @WithMockUser("change-password-wrong-existing-password")
@Throws(Exception::class) fun testChangePasswordWrongExistingPassword() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-wrong-existing-password",
            email = "change-password-wrong-existing-password@example.com"
        )

        userRepository.saveAndFlush(user)

        restAccountMockMvc.perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(PasswordChangeDTO("1$currentPassword", "new password")))
        )
            .andExpect(status().isBadRequest)

        val updatedUser = userRepository.findOneByLogin("change-password-wrong-existing-password").orElse(null)
        assertThat(passwordEncoder.matches("new password", updatedUser.password)).isFalse()
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.password)).isTrue()
    }

    @Test
    @Transactional
    @WithMockUser("change-password")
@Throws(Exception::class) fun testChangePassword() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password",
            email = "change-password@example.com"
        )

        userRepository.saveAndFlush(user)

        restAccountMockMvc.perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, "new password")))
        )
            .andExpect(status().isOk)

        val updatedUser = userRepository.findOneByLogin("change-password").orElse(null)
        assertThat(passwordEncoder.matches("new password", updatedUser.password)).isTrue()
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-small")
@Throws(Exception::class) fun testChangePasswordTooSmall() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-too-small",
            email = "change-password-too-small@example.com"
        )

        userRepository.saveAndFlush(user)

        val newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1)

        restAccountMockMvc.perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, newPassword)))
        )
            .andExpect(status().isBadRequest)

        val updatedUser = userRepository.findOneByLogin("change-password-too-small").orElse(null)
        assertThat(updatedUser.password).isEqualTo(user.password)
    }

    @Test
    @Transactional
    @WithMockUser("change-password-too-long")
@Throws(Exception::class) fun testChangePasswordTooLong() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-too-long",
            email = "change-password-too-long@example.com"
        )

        userRepository.saveAndFlush(user)

        val newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1)

        restAccountMockMvc.perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, newPassword)))
        )
            .andExpect(status().isBadRequest)

        val updatedUser = userRepository.findOneByLogin("change-password-too-long").orElse(null)
        assertThat(updatedUser.password).isEqualTo(user.password)
    }

    @Test
    @Transactional
    @WithMockUser("change-password-empty")
@Throws(Exception::class) fun testChangePasswordEmpty() {
        val currentPassword = RandomStringUtils.random(60)
        val user = User(
            password = passwordEncoder.encode(currentPassword),
            login = "change-password-empty",
            email = "change-password-empty@example.com"
        )

        userRepository.saveAndFlush(user)

        restAccountMockMvc.perform(
            post("/api/account/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(PasswordChangeDTO(currentPassword, "")))
        )
            .andExpect(status().isBadRequest)

        val updatedUser = userRepository.findOneByLogin("change-password-empty").orElse(null)
        assertThat(updatedUser.password).isEqualTo(user.password)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRequestPasswordReset() {
        val user = User(
            password = RandomStringUtils.random(60),
            activated = true,
            login = "password-reset",
            email = "password-reset@example.com"
        )

        userRepository.saveAndFlush(user)

        restAccountMockMvc.perform(
            post("/api/account/reset-password/init")
                .content("password-reset@example.com")
        )
            .andExpect(status().isOk)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testRequestPasswordResetUpperCaseEmail() {
        val user = User(
            password = RandomStringUtils.random(60),
            activated = true,
            login = "password-reset-upper-case",
            email = "password-reset-upper-case@example.com"
        )

        userRepository.saveAndFlush(user)

        restAccountMockMvc.perform(
            post("/api/account/reset-password/init")
                .content("password-reset@EXAMPLE.COM")
        )
            .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testRequestPasswordResetWrongEmail() {
        restAccountMockMvc.perform(
            post("/api/account/reset-password/init")
                .content("password-reset-wrong-email@example.com")
        )
            .andExpect(status().isOk)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testFinishPasswordReset() {
        val user = User(
            password = RandomStringUtils.random(60),
            login = "finish-password-reset",
            email = "finish-password-reset@example.com",
            resetDate = Instant.now().plusSeconds(60),
            resetKey = "reset key"
        )

        userRepository.saveAndFlush(user)

        val keyAndPassword = KeyAndPasswordVM(key = user.resetKey, newPassword = "new password")

        restAccountMockMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(keyAndPassword))
        )
            .andExpect(status().isOk)

        val updatedUser = userRepository.findOneByLogin(user.login!!).orElse(null)
        assertThat(passwordEncoder.matches(keyAndPassword.newPassword, updatedUser.password)).isTrue()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testFinishPasswordResetTooSmall() {
        val user = User(
            password = RandomStringUtils.random(60),
            login = "finish-password-reset-too-small",
            email = "finish-password-reset-too-small@example.com",
            resetDate = Instant.now().plusSeconds(60),
            resetKey = "reset key too small"
        )

        userRepository.saveAndFlush(user)

        val keyAndPassword = KeyAndPasswordVM(key = user.resetKey, newPassword = "foo")

        restAccountMockMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(keyAndPassword))
        )
            .andExpect(status().isBadRequest)

        val updatedUser = userRepository.findOneByLogin(user.login!!).orElse(null)
        assertThat(passwordEncoder.matches(keyAndPassword.newPassword, updatedUser.password)).isFalse()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testFinishPasswordResetWrongKey() {
        val keyAndPassword = KeyAndPasswordVM(key = "wrong reset key", newPassword = "new password")

        restAccountMockMvc.perform(
            post("/api/account/reset-password/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(keyAndPassword))
        )
            .andExpect(status().isInternalServerError)
    }
}
