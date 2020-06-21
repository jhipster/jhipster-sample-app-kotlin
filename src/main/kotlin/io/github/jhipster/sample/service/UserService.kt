package io.github.jhipster.sample.service

import io.github.jhipster.sample.config.ANONYMOUS_USER
import io.github.jhipster.sample.config.DEFAULT_LANGUAGE
import io.github.jhipster.sample.domain.User
import io.github.jhipster.sample.repository.AuthorityRepository
import io.github.jhipster.sample.repository.UserRepository
import io.github.jhipster.sample.security.USER
import io.github.jhipster.sample.security.getCurrentUserLogin
import io.github.jhipster.sample.service.dto.UserDTO
import io.github.jhipster.security.RandomUtil
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for managing users.
 */
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authorityRepository: AuthorityRepository,
    private val cacheManager: CacheManager
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun activateRegistration(key: String): Optional<User> {
        log.debug("Activating user for activation key {}", key)
        return userRepository.findOneByActivationKey(key)
            .map { user ->
                // activate given user for the registration key.
                user.activated = true
                user.activationKey = null
                clearUserCaches(user)
                log.debug("Activated user: {}", user)
                user
            }
    }

    fun completePasswordReset(newPassword: String, key: String): Optional<User> {
        log.debug("Reset user password for reset key {}", key)
        return userRepository.findOneByResetKey(key)
            .filter { user -> user.resetDate?.isAfter(Instant.now().minusSeconds(86400)) ?: false }
            .map {
                it.password = passwordEncoder.encode(newPassword)
                it.resetKey = null
                it.resetDate = null
                clearUserCaches(it)
                it
            }
    }

    fun requestPasswordReset(mail: String): Optional<User> {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::activated)
            .map {
                it.resetKey = RandomUtil.generateResetKey()
                it.resetDate = Instant.now()
                clearUserCaches(it)
                it
            }
    }

    fun registerUser(userDTO: UserDTO, password: String): User {
        val login = userDTO.login ?: throw IllegalArgumentException("Empty login not allowed")
        val email = userDTO.email
        userRepository.findOneByLogin(login.toLowerCase()).ifPresent { existingUser ->
            val removed = removeNonActivatedUser(existingUser)
            if (!removed) {
                throw UsernameAlreadyUsedException()
            }
        }
        userRepository.findOneByEmailIgnoreCase(email).ifPresent { existingUser ->
            val removed = removeNonActivatedUser(existingUser)
            if (!removed) {
                throw EmailAlreadyUsedException()
            }
        }
        val newUser = User()
        val encryptedPassword = passwordEncoder.encode(password)
        newUser.apply {
            this.login = login.toLowerCase()
            // new user gets initially a generated password
            this.password = encryptedPassword
            firstName = userDTO.firstName
            lastName = userDTO.lastName
            this.email = email?.toLowerCase()
            imageUrl = userDTO.imageUrl
            langKey = userDTO.langKey
            // new user is not active
            activated = false
            // new user gets registration key
            activationKey = RandomUtil.generateActivationKey()
            authorities = mutableSetOf()
            authorityRepository.findById(USER).ifPresent { authorities.add(it) }
        }
        userRepository.save(newUser)
        clearUserCaches(newUser)
        log.debug("Created Information for User: {}", newUser)
        return newUser
    }

    private fun removeNonActivatedUser(existingUser: User): Boolean {
        if (existingUser.activated) {
            return false
        }
        userRepository.delete(existingUser)
        userRepository.flush()
        clearUserCaches(existingUser)
        return true
    }

    fun createUser(userDTO: UserDTO): User {
        val user = User(
            login = userDTO.login?.toLowerCase(),
            firstName = userDTO.firstName,
            lastName = userDTO.lastName,
            email = userDTO.email?.toLowerCase(),
            imageUrl = userDTO.imageUrl,
            // default language
            langKey = userDTO.langKey ?: DEFAULT_LANGUAGE,
            password = passwordEncoder.encode(RandomUtil.generatePassword()),
            resetKey = RandomUtil.generateResetKey(),
            resetDate = Instant.now(),
            activated = true,
            authorities = userDTO.authorities?.let { authorities ->
                authorities.map { authorityRepository.findById(it) }
                        .filter { it.isPresent }
                        .mapTo(mutableSetOf()) { it.get() }
            } ?: mutableSetOf()
        )
        userRepository.save(user)
        clearUserCaches(user)
        log.debug("Created Information for User: {}", user)
        return user
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    fun updateUser(userDTO: UserDTO): Optional<UserDTO> {
        return Optional.of(userRepository.findById(userDTO.id!!))
            .filter(Optional<User>::isPresent)
            .map { it.get() }
            .map { user ->
                clearUserCaches(user)
                user.apply {
                    login = userDTO.login!!.toLowerCase()
                    firstName = userDTO.firstName
                    lastName = userDTO.lastName
                    email = userDTO.email?.toLowerCase()
                    imageUrl = userDTO.imageUrl
                    activated = userDTO.activated
                    langKey = userDTO.langKey
                }
                val managedAuthorities = user.authorities
                managedAuthorities.clear()
                userDTO.authorities?.apply {
                    this.asSequence()
                        .map { authorityRepository.findById(it) }
                        .filter { it.isPresent }
                        .mapTo(managedAuthorities) { it.get() }
                }
                clearUserCaches(user)
                log.debug("Changed Information for User: {}", user)
                user
            }
            .map { UserDTO(it) }
    }

    fun deleteUser(login: String) {
        userRepository.findOneByLogin(login).ifPresent { user ->
            userRepository.delete(user)
            clearUserCaches(user)
            log.debug("Deleted User: {}", user)
        }
    }
    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName last name of user.
     * @param email email id of user.
     * @param langKey language key.
     * @param imageUrl image URL of user.
     */
    fun updateUser(firstName: String?, lastName: String?, email: String?, langKey: String?, imageUrl: String?) {
        getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent {

                it.firstName = firstName
                it.lastName = lastName
                it.email = email?.toLowerCase()
                it.langKey = langKey
                it.imageUrl = imageUrl
                clearUserCaches(it)
                log.debug("Changed Information for User: {it}")
            }
    }

    @Transactional
    fun changePassword(currentClearTextPassword: String, newPassword: String) {
        getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
                .ifPresent { user ->
                    val currentEncryptedPassword = user.password
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw InvalidPasswordException()
                    }
                    val encryptedPassword = passwordEncoder.encode(newPassword)
                    user.password = encryptedPassword
                clearUserCaches(user)
                log.debug("Changed password for User: {}", user)
                user
            }
    }

    @Transactional(readOnly = true)
    fun getAllManagedUsers(pageable: Pageable): Page<UserDTO> =
        userRepository.findAllByLoginNot(pageable, ANONYMOUS_USER).map { UserDTO(it) }

    @Transactional(readOnly = true)
    fun getUserWithAuthoritiesByLogin(login: String): Optional<User> =
        userRepository.findOneWithAuthoritiesByLogin(login)

    @Transactional(readOnly = true)
    fun getUserWithAuthorities(): Optional<User> =
        getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin)

    /**
     * Not activated users should be automatically deleted after 3 days.
     *
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    fun removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                Instant.now().minus(3, ChronoUnit.DAYS)
            )
            .forEach { user ->
                log.debug("Deleting not activated user {}", user.login)
                userRepository.delete(user)
                clearUserCaches(user)
            }
    }

    /**
     * @return a list of all the authorities
     */
    @Transactional(readOnly = true)
    fun getAuthorities() =
        authorityRepository.findAll().asSequence().map { it.name }.filterNotNullTo(mutableListOf())

    private fun clearUserCaches(user: User) {
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)?.evict(user.login!!)
        if (user.email != null) {
            cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)?.evict(user.email)
        }
    }
}
