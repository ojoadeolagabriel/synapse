package com.synapse.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.synapse.runner.dto.PayinOrderFilePayload
import com.synapse.task.TaskService
import com.synapse.task.config.SynapseTask
import com.synapse.task.event.SynapseEvent

import java.time.Clock

class RunnerApp {
    static ObjectMapper mapper = new ObjectMapper()

    static void main(String[] args) {
        def taskService = new TaskService()

        //handle things
        taskService.deployTaskProcessor("topic.handler", {
            payload -> System.out.println("received payload: $payload")
        })

        //deploy new task
        taskService.deployTask(new SynapseTask({
            SynapseEvent event = new SynapseEvent()
            event.setTopic("topic.handler")
            event.setKey("PAYIN_" + Clock.systemDefaultZone().millis())
            event.setMessage(buildPayload())
            return event
        }))
    }

    /**
     * build payload
     * @return
     */
    private static String buildPayload() {
        PayinOrderFilePayload payload = new PayinOrderFilePayload()
        payload.setPspReference("PSP10001" + Clock.systemDefaultZone().millis().toString() + "REF")
        payload.setRequestId(Clock.systemDefaultZone().millis().toString())

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.writeValueAsString(payload)
    }
}
