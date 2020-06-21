package io.github.jhipster.sample.config

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WebConfigurerTestController {

    @GetMapping("/api/test-cors")
    fun testCorsOnApiPath() = Unit

    @GetMapping("/test/test-cors")
    fun testCorsOnOtherPath() = Unit
}
