package io.github.jhipster.sample.web.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ClientForwardController {
    /**
     * Forwards any unmapped paths (except those containing a period) to the client `index.html`.
     * @return forward to client `index.html`.
     */
    @GetMapping(value = ["/**/{path:[^\\.]*}"])
    fun forward() = "forward:/"
}
