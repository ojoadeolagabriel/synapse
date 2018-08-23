package com.synapse.task;

import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.config.SynapseTask;
import com.synapse.task.handler.KafkaTaskResponseHandler;

public class TaskService {

    KafkaSynapseConfig config;

    TaskService() {
        config = new KafkaSynapseConfig();
    }

    public void deployTask(SynapseTask task) {
        task.setConfig(config);
        config.getVertx().deployVerticle(task);
    }

    public void deployTaskProcessor(String id, KafkaTaskResponseHandler handler) {
        config.kafkaConsumer().subscribe(id, subscribe -> {
            if (subscribe.succeeded()) {
                System.out.println("successfully subscribed to: " + id);
            }
        });

        config.kafkaConsumer().handler(record -> {
            handler.handle(record.value());
        });
    }
}
