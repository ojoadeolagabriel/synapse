package com.synapse.runner.service

import com.synapse.runner.builder.PayinOrderFilePayloadBuilder
import com.synapse.runner.dto.PayinOrderFilePayload
import com.synapse.task.TaskService
import com.synapse.task.context.EventState
import com.synapse.task.event.CompletionEvent
import com.synapse.task.event.Event
import com.synapse.task.util.Constants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.Clock
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

@Component
class PaymentTaskService {
	@Autowired
	TaskService taskService

	@PostConstruct
	void init() {

		//post params
		def testTopic = "topic.handler-x"

		//handle things
		taskService.completeTask(testTopic, {
			payload ->
				try {
					PayinOrderFilePayload load = Constants.mapper.readValue(payload, PayinOrderFilePayload)
					return load ? EventState.Success : EventState.Failed
				} catch (Exception e) {
					e.printStackTrace()
					return EventState.Failed
				}
		})

		//track process completions per second
		AtomicLong counter = new AtomicLong()
		taskService.config.getVertx().setPeriodic(1000, {
			System.out.println("syna-mon count: " + counter.get())
			counter.set(0)
		})

		//periodic task generation
		taskService.config.getVertx().setPeriodic(1, { handler ->

			//deploy new task
			taskService.startTask({
				Event event = new Event()
				event.setTopic(testTopic)
				addHeaders(event)
				event.setKey(buildKey())
				event.setMessage(PayinOrderFilePayloadBuilder.payloadBuilder())
				return event
			}, { CompletionEvent body ->
				taskService.config.getVertx().setTimer(1,  {
					counter.addAndGet(1)
					switch (body.state) {
						case EventState.Success:
						case EventState.DuplicateDetected:
						case EventState.Closed:
						case EventState.Retry:
							break
						case EventState.Failed:
						default:
							return
					}
				})
			})
		})
	}

	def addHeaders(Event event) {
		event?.putHeader("timestamp", Date.from(Instant.now()).toString())
	}

	private String buildKey() {
		def messagePrefix = "PREFIX"
		String.format("%s_%s", messagePrefix, Clock.systemDefaultZone().millis())
	}
}
