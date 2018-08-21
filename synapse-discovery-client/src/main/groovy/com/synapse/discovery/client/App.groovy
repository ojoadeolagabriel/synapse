package com.synapse.discovery.client

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class App {
    static void main(String[] args) {
        SpringApplication.run(App, args)
    }
}
