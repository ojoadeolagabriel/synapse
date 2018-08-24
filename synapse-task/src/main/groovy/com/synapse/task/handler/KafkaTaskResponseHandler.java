package com.synapse.task.handler;

import com.synapse.task.context.EventState;

public interface KafkaTaskResponseHandler {
    EventState handle(String payload);
}
