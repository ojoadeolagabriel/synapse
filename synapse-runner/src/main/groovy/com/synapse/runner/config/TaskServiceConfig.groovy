package com.synapse.runner.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.synapse.task.TaskService
import io.vertx.core.json.JsonObject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TaskServiceConfig {
	@Bean
	TaskService taskService() {
		def taskService = new TaskService("localhost:9092",
				"default_group_1",
				new JsonObject(){{
					put("url", "jdbc:mysql://localhost:3306/fx?user=swd&password=swd")
					put("driver_class", "com.mysql.jdbc.Driver")
					put("max_pool_size", 2000)
				}})
		return taskService
	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper()
		return mapper
	}
}
