package com.synapse.io.config.server.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config")
class CheckConfigController {
    @Autowired
    Environment environment

    @Value("message")
    String message

    @RequestMapping("/home")
    String home(){
        return environment.getProperty("spring.cloud.config.server.git.uri")
    }

    @RequestMapping("/message")
    String message(){

        return environment.getProperty("message")
    }
}
