package com.synapse.task.event;

import com.synapse.task.context.EventState;

public class CompletionEvent {
    private String message;
    private EventState state;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }
}
