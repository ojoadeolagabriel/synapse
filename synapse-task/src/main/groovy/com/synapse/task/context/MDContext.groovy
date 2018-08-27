package com.synapse.task.context

import com.synapse.task.event.Event
import com.synapse.task.handler.MDCHandler
import org.slf4j.MDC

class MDContext {
    private MDContext() {

    }

    static MDContext context() {
        return new MDContext()
    }

    Event event

    MDContext setEvent(Event event) {
        this.event = event
        return this
    }

    void handle(MDCHandler handler) {
        handler.handle()
        if (event) {
            MDC.put("event.topic", event.topic)
            MDC.put("event.key", event.key)
        }
    }
}
