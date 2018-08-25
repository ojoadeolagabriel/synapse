package com.synapse.runner.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.synapse.task.TaskService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TaskServiceConfig {
    @Bean
    TaskService taskService() {
        def taskService = new TaskService("localhost:9092", "default_group")
        return taskService
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper()
        return mapper
    }
}
