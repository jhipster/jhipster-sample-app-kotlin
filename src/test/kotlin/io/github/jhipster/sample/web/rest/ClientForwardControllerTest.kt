package io.github.jhipster.sample.web.rest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Unit tests for the [ClientForwardController] REST controller.
 */
class ClientForwardControllerTest {

    private lateinit var restMockMvc: MockMvc

    @BeforeEach
    fun setup() {
        val clientForwardController = ClientForwardController()
        this.restMockMvc = MockMvcBuilders
            .standaloneSetup(clientForwardController, TestController())
            .build()
    }

    @Test
    @Throws(Exception::class)
    fun getBackendEndpoint() {
        restMockMvc.perform(get("/test"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
            .andExpect(content().string("test"))
    }

    @Test
    @Throws(Exception::class)
    fun getClientEndpoint() {
        val perform = restMockMvc.perform(get("/non-existant-mapping"))
        perform
            .andExpect(status().isOk)
            .andExpect(forwardedUrl("/"))
    }

    @Test
    @Throws(Exception::class)
    fun getNestedClientEndpoint() {
        restMockMvc.perform(get("/admin/user-management"))
            .andExpect(status().isOk)
            .andExpect(forwardedUrl("/"))
    }

    @RestController
    inner class TestController {
        @RequestMapping(value = ["/test"])
        fun test() = "test"
    }
}
