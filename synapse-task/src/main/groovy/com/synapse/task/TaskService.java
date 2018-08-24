package com.synapse.task;

import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.context.EventState;
import com.synapse.task.handler.ProcessCompletionHandler;
import com.synapse.task.event.CompletionEvent;
import com.synapse.task.context.MessageContext;
import com.synapse.task.handler.KafkaTaskResponseHandler;

public class TaskService {
    KafkaSynapseConfig config;

    TaskService() {
        config = new KafkaSynapseConfig("localhost:9092", "default_group");
    }

    public void startTask(MessageContext task, ProcessCompletionHandler onProcessCompletionHandler) {
        task.setConfig(config);

        config.getVertx().deployVerticle(task, onDeployHandler -> {
            persistState(task.synapseEvent.getKey(), EventState.Pending, task.synapseEvent.getMessage());

            config.getVertx().eventBus().consumer(task.synapseEvent.getKey(), handler -> {
                try {
                    EventState state = EventState.valueOf(handler.body().toString());
                    CompletionEvent data = new CompletionEvent();
                    data.setState(state);
                    onProcessCompletionHandler.handle(data);
                    persistState(task.synapseEvent.getKey(), EventState.Closed, task.synapseEvent.getMessage());
                } catch (Exception e) {
                    persistState(task.synapseEvent.getKey(), EventState.Retry, task.synapseEvent.getMessage());
                }
            });
        });
    }

    public void completeTask(String id, KafkaTaskResponseHandler handler) {
        config.kafkaConsumer().subscribe(id, subscribe -> {
            if (subscribe.succeeded()) {
                config.kafkaConsumer().handler(record -> {
                    EventState result = handler.handle(record.value());
                    persistState(id, result, record.value());
                    config.getVertx().eventBus().send(record.key(), result.name());
                });
            }
        });
    }

    private void persistState(String id, EventState result, String value) {
        System.out.println(String.format("persisting.. %s, %s, %s", id, result.name(), value));
    }
}
