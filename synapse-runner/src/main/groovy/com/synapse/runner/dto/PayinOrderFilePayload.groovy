package com.synapse.runner.dto

import com.fasterxml.jackson.annotation.JsonFormat

import java.time.Instant

class PayinOrderFilePayload {
    private String requestId
    private String pspReference

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    public Date date

    Date getDate() {
        return date
    }

    void setDate(Date date) {
        this.date = date
    }

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
