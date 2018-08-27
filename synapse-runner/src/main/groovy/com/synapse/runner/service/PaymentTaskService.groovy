package com.synapse.runner.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.synapse.runner.builder.PayinOrderFilePayloadBuilder
import com.synapse.runner.dto.PayinOrderFilePayload
import com.synapse.task.TaskService
import com.synapse.task.context.EventState
import com.synapse.task.event.CompletionEvent
import com.synapse.task.event.SynapseEvent
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

    @Autowired
    ObjectMapper mapper

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
        taskService.config.getVertx().setPeriodic(1000, { handler ->
            //deploy new task
            taskService.executeTask({
                SynapseEvent event = new SynapseEvent()
                event.setTopic(testTopic)
                addHeaders(event)
                event.setKey(buildKey())
                event.setMessage(PayinOrderFilePayloadBuilder.payloadBuilder())
                return event
            }, { CompletionEvent body ->
                System.out.println("completing... " + body.state)
                switch (body.state) {
                    case EventState.Success:
                    case EventState.Closed:
                        break
                    case EventState.Failed:
                    default:
                        return
                }
            })
        })
    }

    def addHeaders(SynapseEvent event) {
        event?.putHeader("timestamp", Date.from(Instant.now()).toString())
    }

    private String buildKey() {
        def messagePrefix
        String.format("%s_%s", messagePrefix, Clock.systemDefaultZone().millis())
    }
}
