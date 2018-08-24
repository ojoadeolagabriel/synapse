package com.synapse.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.synapse.runner.dto.PayinOrderFilePayload
import com.synapse.task.TaskService
import com.synapse.task.context.EventState
import com.synapse.task.event.CompletionEvent
import com.synapse.task.context.MessageContext
import com.synapse.task.event.SynapseEvent

import java.time.Clock

class RunnerApp {

    static ObjectMapper mapper = new ObjectMapper()
    static void main(String[] args) {
        def taskService = new TaskService()
        def testTopic = "topic.handler-x"
        def messagePrefix = "PAYIN"

        //handle things
        taskService.completeTask(testTopic, {
            payload ->
                System.out.println("received payload: $payload")
                PayinOrderFilePayload load = mapper.readValue(payload, PayinOrderFilePayload)
                return load ? EventState.Success : EventState.Failed
        })

        //deploy new task
        taskService.startTask(new MessageContext({
            SynapseEvent event = new SynapseEvent()
            event.setTopic(testTopic)
            event.setKey(String.format("%s_%s", messagePrefix, Clock.systemDefaultZone().millis()))
            event.setMessage(payloadBuilder())
            return event
        }), { CompletionEvent body ->
            System.out.println("boom.. its here: $body.state")
        })
    }

    /**
     * build payload
     * @return
     */
    private static String payloadBuilder() {
        PayinOrderFilePayload payload = new PayinOrderFilePayload()
        payload.setPspReference("PSP10001" + Clock.systemDefaultZone().millis().toString() + "REF")
        payload.setRequestId(Clock.systemDefaultZone().millis().toString())

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.writeValueAsString(payload)
    }
}
