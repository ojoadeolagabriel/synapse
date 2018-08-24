package com.synapse.task.handler;

public interface KafkaTaskResponseHandler {
    boolean handle(String payload);
}
