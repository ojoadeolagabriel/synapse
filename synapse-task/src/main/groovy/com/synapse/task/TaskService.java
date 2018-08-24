package com.synapse.task;

import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.handler.ProcessCompletionHandler;
import com.synapse.task.event.CompletionEvent;
import com.synapse.task.context.MessageContext;
import com.synapse.task.handler.KafkaTaskResponseHandler;

public class TaskService {

    KafkaSynapseConfig config;

    TaskService() {
        config = new KafkaSynapseConfig();
    }

    public void startTask(MessageContext task, ProcessCompletionHandler onProcessCompletionHandler) {
        task.setConfig(config);

        config.getVertx().deployVerticle(task, onDeployHandler -> {
            config.getVertx().eventBus().consumer(task.synapseEvent.getKey(), handler -> {
                boolean result = Boolean.valueOf(handler.body() != null ? handler.body().toString() : "false");
                CompletionEvent data = new CompletionEvent();
                data.setSuccess(result);
                onProcessCompletionHandler.handle(data);
            });
        });
    }

    public void completeTask(String id, KafkaTaskResponseHandler handler) {
        config.kafkaConsumer().subscribe(id, subscribe -> {
            if (subscribe.succeeded()) {
                config.kafkaConsumer().handler(record -> {
                    boolean result = handler.handle(record.value());
                    config.getVertx().eventBus().send(record.key(), result);
                });
            }
        });
    }
}
