package io.github.jhipster.sample.web.rest.errors

import javax.validation.Valid
import javax.validation.constraints.NotNull
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/exception-translator-test")
class ExceptionTranslatorTestController {

    @GetMapping("/concurrency-failure")
    fun concurrencyFailure(): Unit = throw ConcurrencyFailureException("test concurrency failure")

    @PostMapping("/method-argument")
    fun methodArgument(@Valid @RequestBody testDTO: TestDTO) = Unit

    @GetMapping("/missing-servlet-request-part")
    fun missingServletRequestPartException(@RequestPart part: String) = Unit

    @GetMapping("/missing-servlet-request-parameter")
    fun missingServletRequestParameterException(@RequestParam param: String) = Unit

    @GetMapping("/access-denied")
    fun accessdenied(): Unit = throw AccessDeniedException("test access denied!")

    @GetMapping("/unauthorized")
    fun unauthorized(): Unit = throw BadCredentialsException("test authentication failed!")

    @GetMapping("/response-status")
    fun exceptionWithResponseStatus(): Unit = throw TestResponseStatusException()

    @GetMapping("/internal-server-error")
    fun internalServerError(): Unit = throw RuntimeException()

    class TestDTO {
        @field:NotNull
        var test: String? = null
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "test response status")
    class TestResponseStatusException : RuntimeException()
}
