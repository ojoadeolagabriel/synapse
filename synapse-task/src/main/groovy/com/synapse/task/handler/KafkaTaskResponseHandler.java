package com.synapse.task.handler;

import com.synapse.task.event.SynapseEvent;

public interface KafkaTaskResponseHandler {
    SynapseEvent handle(String payload);
}
