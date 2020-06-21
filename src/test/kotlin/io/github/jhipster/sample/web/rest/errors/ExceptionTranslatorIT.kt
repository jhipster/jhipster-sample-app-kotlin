package io.github.jhipster.sample.web.rest.errors

import io.github.jhipster.sample.JhipsterApp
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Integration tests [ExceptionTranslator] controller advice.
 */
@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest(classes = [JhipsterApp::class])
class ExceptionTranslatorIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun testConcurrencyFailure() {
        mockMvc.perform(get("/api/exception-translator-test/concurrency-failure"))
            .andExpect(status().isConflict)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value(ERR_CONCURRENCY_FAILURE))
    }

    @Test
    @Throws(Exception::class)
    fun testMethodArgumentNotValid() {
        mockMvc.perform(post("/api/exception-translator-test/method-argument").content("{}").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value(ERR_VALIDATION))
            .andExpect(jsonPath("\$.fieldErrors.[0].objectName").value("test"))
            .andExpect(jsonPath("\$.fieldErrors.[0].field").value("test"))
            .andExpect(jsonPath("\$.fieldErrors.[0].message").value("NotNull"))
    }

    @Test
    @Throws(Exception::class)
    fun testMissingServletRequestPartException() {
        mockMvc.perform(get("/api/exception-translator-test/missing-servlet-request-part"))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.400"))
    }

    @Test
    @Throws(Exception::class)
    fun testMissingServletRequestParameterException() {
        mockMvc.perform(get("/api/exception-translator-test/missing-servlet-request-parameter"))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.400"))
    }

    @Test
    @Throws(Exception::class)
    fun testAccessDenied() {
        mockMvc.perform(get("/api/exception-translator-test/access-denied"))
            .andExpect(status().isForbidden)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.403"))
            .andExpect(jsonPath("\$.detail").value("test access denied!"))
    }

    @Test
    @Throws(Exception::class)
    fun testUnauthorized() {
        mockMvc.perform(get("/api/exception-translator-test/unauthorized"))
            .andExpect(status().isUnauthorized)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.401"))
            .andExpect(jsonPath("\$.path").value("/api/exception-translator-test/unauthorized"))
            .andExpect(jsonPath("\$.detail").value("test authentication failed!"))
    }

    @Test
    @Throws(Exception::class)
    fun testMethodNotSupported() {
        mockMvc.perform(post("/api/exception-translator-test/access-denied"))
            .andExpect(status().isMethodNotAllowed)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.405"))
            .andExpect(jsonPath("\$.detail").value("Request method 'POST' not supported"))
    }

    @Test
    @Throws(Exception::class)
    fun testExceptionWithResponseStatus() {
        mockMvc.perform(get("/api/exception-translator-test/response-status"))
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.400"))
            .andExpect(jsonPath("\$.title").value("test response status"))
    }

    @Test
    @Throws(Exception::class)
    fun testInternalServerError() {
        mockMvc.perform(get("/api/exception-translator-test/internal-server-error"))
            .andExpect(status().isInternalServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("\$.message").value("error.http.500"))
            .andExpect(jsonPath("\$.title").value("Internal Server Error"))
    }
}
