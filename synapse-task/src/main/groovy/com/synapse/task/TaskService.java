package com.synapse.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.context.EventState;
import com.synapse.task.event.SynapseEvent;
import com.synapse.task.handler.ProcessCompletionHandler;
import com.synapse.task.event.CompletionEvent;
import com.synapse.task.context.MessagePipeline;
import com.synapse.task.handler.KafkaTaskResponseHandler;
import com.synapse.task.handler.SynapseTaskHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskService {
    public KafkaSynapseConfig config;
    MessagePipeline messagePipeline;
    ObjectMapper mapper = new ObjectMapper();
    private static final Object lock = new Object();

    TaskService(String bootStrapServers, String consumerGroup) {
        config = new KafkaSynapseConfig(bootStrapServers, consumerGroup);
    }

    public void executeTask(SynapseTaskHandler eventBuildHandler, ProcessCompletionHandler onProcessCompletionHandler) {
        SynapseEvent event = eventBuildHandler.handle();
        synchronized (lock) {
            if (messagePipeline == null) {
                messagePipeline = new MessagePipeline(config);

                //deploy pipeline handler
                config.getVertx().deployVerticle(messagePipeline, deployCompletionHandler -> {
                    System.out.println("Task message pipeline: " + event.getTopic() + " active");
                });
            }
        }

        try {
            String payload = mapper.writeValueAsString(event);

            //setup close
            config.getVertx().eventBus().consumer(event.getKey(), handler -> {
                try {
                    EventState state = EventState.valueOf(handler.body().toString());
                    CompletionEvent data = new CompletionEvent();
                    data.setState(state);
                    onProcessCompletionHandler.handle(data);
                    persistState(event.getKey(), EventState.Closed, event.getMessage());
                } catch (Exception e) {
                    persistState(event.getKey(), EventState.Retry, event.getMessage());
                }
            });

            config.getVertx().eventBus().send("::synapse.message.pipeline::", payload, replyHandler -> {
                if (replyHandler.succeeded()) {

                } else {
                    System.out.println("could not push: " + payload + " reason: " + replyHandler.cause().getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void completeTask(String id, KafkaTaskResponseHandler completionHandler) {
        config.kafkaConsumer().subscribe(id, subscribe -> {
            if (subscribe.succeeded()) {
                config.kafkaConsumer().handler(record -> {
                    EventState result = completionHandler.handle(record.value());
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
