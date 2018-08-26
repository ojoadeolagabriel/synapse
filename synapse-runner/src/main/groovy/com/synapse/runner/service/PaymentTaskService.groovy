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
import java.util.concurrent.atomic.AtomicLong

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
        def messagePrefix = "PAYIN"

        //handle things
        taskService.completeTask(testTopic, {
            payload ->
                System.out.println("completion received for payload: $payload")
                PayinOrderFilePayload load = mapper.readValue(payload, PayinOrderFilePayload)
                return load ? EventState.Success : EventState.Failed
        })

        taskService.config.getVertx().setPeriodic(5000, { handler ->

            //deploy new task
            taskService.executeTask({
                SynapseEvent event = new SynapseEvent()
                event.setTopic(testTopic)
                event.setKey(String.format("%s_%s", messagePrefix, Clock.systemDefaultZone().millis()))
                event.setMessage(PayinOrderFilePayloadBuilder.payloadBuilder())
                return event
            }, { CompletionEvent body ->
                if (body.state == EventState.Closed) {

                } else if (body.state == EventState.Failed) {

                }
            })
        })
    }
}
