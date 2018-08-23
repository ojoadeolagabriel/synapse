package com.synapse.runner.dto

import com.fasterxml.jackson.annotation.JsonFormat

import java.time.Instant

class PayinOrderFilePayload {
    private String requestId
    private String pspReference

    Instant getCurrentTimestamp() {
        return currentTimestamp
    }

    void setCurrentTimestamp(Instant currentTimestamp) {
        this.currentTimestamp = currentTimestamp
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    public Date currentTimestamp

    String getRequestId() {
        return requestId
    }

    void setRequestId(String requestId) {
        this.requestId = requestId
    }

    String getPspReference() {
        return pspReference
    }

    void setPspReference(String pspReference) {
        this.pspReference = pspReference
    }
}
