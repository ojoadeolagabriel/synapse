package com.synapse.runner.builder

import com.synapse.runner.dto.PayinOrderFilePayload
import com.synapse.task.util.Constants

import java.time.Clock
import java.time.Instant

class PayinOrderFilePayloadBuilder {

    static String payloadBuilder() {
        PayinOrderFilePayload payload = new PayinOrderFilePayload()
        payload.setPspReference("PREF001" + Clock.systemDefaultZone().millis().toString() + "VIM")
        payload.setRequestId(Clock.systemDefaultZone().millis().toString())
        payload.setDate(Date.from(Instant.now()))
        Constants.mapper.writeValueAsString(payload)
    }
}
