package com.synapse.runner

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class RunnerApp {
    private static final Logger logger= LoggerFactory.getLogger(RunnerApp)
    static void main(String[] args) {
        logger.debug("wowzer...")
        SpringApplication.run(RunnerApp, args)
    }
}
