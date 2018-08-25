package com.synapse.runner.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.synapse.runner.dto.PayinOrderFilePayload
import com.synapse.task.TaskService
import com.synapse.task.context.EventState
import com.synapse.task.context.MessageContext
import com.synapse.task.event.CompletionEvent
import com.synapse.task.event.SynapseEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.Clock

@Component
class PaymentTaskService {
    @Autowired
    TaskService taskService
    @Autowired
    ObjectMapper mapper

    @PostConstruct
    void init(){
        //post params
        def testTopic = "topic.handler-x"
        def messagePrefix = "PAYIN"

        //handle things
        taskService.taskCompletion(testTopic, {
            payload ->
                System.out.println("received payload: $payload")
                PayinOrderFilePayload load = mapper.readValue(payload, PayinOrderFilePayload)
                return load ? EventState.Success : EventState.Failed
        })

        //deploy new task
        taskService.taskStarter(new MessageContext({
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
    private String payloadBuilder() {
        PayinOrderFilePayload payload = new PayinOrderFilePayload()
        payload.setPspReference("PSP10001" + Clock.systemDefaultZone().millis().toString() + "REF")
        payload.setRequestId(Clock.systemDefaultZone().millis().toString())

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.writeValueAsString(payload)
    }
}
