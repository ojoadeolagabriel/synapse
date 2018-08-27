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

        //periodic task generation
        taskService.config.getVertx().setPeriodic(5000, { handler ->

            //deploy new task
            taskService.startTask({
                Event event = new Event()
                  event.setTopic(testTopic)
                addHeaders(event)
                event.setKey(buildKey())
                event.setMessage(PayinOrderFilePayloadBuilder.payloadBuilder())
                return event
            }, { CompletionEvent body ->
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
    }

    def addHeaders(Event event) {
        event?.putHeader("timestamp", Date.from(Instant.now()).toString())
    }

    private String buildKey() {
        def messagePrefix = "PREFIX"
        String.format("%s_%s", messagePrefix, Clock.systemDefaultZone().millis())
    }
}
