package io.github.jhipster.sample.service

import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.sample.domain.User
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine

private const val USER = "user"
private const val BASE_URL = "baseUrl"

/**
 * Service for sending emails.
 *
 * We use the [Async] annotation to send emails asynchronously.
 */
@Service
class MailService(
    private val jHipsterProperties: JHipsterProperties,
    private val javaMailSender: JavaMailSender,
    private val messageSource: MessageSource,
    private val templateEngine: SpringTemplateEngine
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    fun sendEmail(to: String, subject: String, content: String, isMultipart: Boolean, isHtml: Boolean) {
        log.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content
        )

        // Prepare message using a Spring helper
        log.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content
        )

        // Prepare message using a Spring helper
        val mimeMessage = javaMailSender.createMimeMessage()
        try {
            MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name()).apply {
                setTo(to)
                setFrom(jHipsterProperties.mail.from)
                setSubject(subject)
                setText(content, isHtml)
            }
            javaMailSender.send(mimeMessage)
            log.debug("Sent email to User '{}'", to)
        } catch (e: MailException) {
            log.warn("Email could not be sent to user '{}'", to, e)
        } catch (e: MessagingException) {
            log.warn("Email could not be sent to user '{}'", to, e)
        }
    }

    @Async
    fun sendEmailFromTemplate(user: User, templateName: String, titleKey: String) {
        if (user.email == null) {
            log.debug("Email doesn't exist for user '{}'", user.login)
            return
        }
        val locale = Locale.forLanguageTag(user.langKey)
        val context = Context(locale).apply {
            setVariable(USER, user)
            setVariable(BASE_URL, jHipsterProperties.mail.baseUrl)
        }
        val content = templateEngine.process(templateName, context)
        val subject = messageSource.getMessage(titleKey, null, locale)
        sendEmail(user.email!!, subject, content, isMultipart = false, isHtml = true)
    }

    @Async
    fun sendActivationEmail(user: User) {
        log.debug("Sending activation email to '{}'", user.email)
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title")
    }

    @Async
    fun sendCreationEmail(user: User) {
        log.debug("Sending creation email to '{}'", user.email)
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title")
    }

    @Async
    fun sendPasswordResetMail(user: User) {
        log.debug("Sending password reset email to '{}'", user.email)
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title")
    }
}
