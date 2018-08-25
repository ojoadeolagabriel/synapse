package com.synapse.runner.builder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.synapse.runner.dto.PayinOrderFilePayload

import java.time.Clock

class PayinOrderFilePayloadBuilder {
    private static ObjectMapper mapper = new ObjectMapper()

    private static String payloadBuilder() {
        PayinOrderFilePayload payload = new PayinOrderFilePayload()
        payload.setPspReference("PSP10001" + Clock.systemDefaultZone().millis().toString() + "REF")
        payload.setRequestId(Clock.systemDefaultZone().millis().toString())

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.writeValueAsString(payload)
    }
}
