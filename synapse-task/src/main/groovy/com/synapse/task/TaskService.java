package com.synapse.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.context.EventState;
import com.synapse.task.context.MessagePipeline;
import com.synapse.task.event.CompletionEvent;
import com.synapse.task.event.SynapseEvent;
import com.synapse.task.handler.KafkaTaskResponseHandler;
import com.synapse.task.handler.ProcessCompletionHandler;
import com.synapse.task.handler.SynapseTaskHandler;
import com.synapse.task.util.Constants;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.extern.slf4j.Slf4j;

import static com.synapse.task.context.EventState.Closed;
import static com.synapse.task.context.EventState.Retry;
import static com.synapse.task.util.Constants.RESULT_POSTFIX;

@Slf4j
public class TaskService {
    public KafkaSynapseConfig config;
    private MessagePipeline messagePipeline;
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

                });
            }
        }

        try {
            //step 3
            String payload = Constants.mapper.writeValueAsString(event);
            config.kafkaResponseConsumer().subscribe(event.getTopic() + RESULT_POSTFIX, handler -> {
                try {
                    if (handler.succeeded()) {
                        config.kafkaResponseConsumer().handler(record -> {
                            EventState state = EventState.valueOf(record.value());
                            CompletionEvent data = new CompletionEvent();
                            data.setState(state);
                            onProcessCompletionHandler.handle(data);

                            System.out.println("processing completion to: " + record.topic() + RESULT_POSTFIX);
                            persistState(event.getKey(), Closed, event.getMessage());
                        });
                    }
                } catch (Exception e) {
                    persistState(event.getKey(), Retry, event.getMessage());
                }
            });

            //send message (1)
            config.getVertx().eventBus().send(Constants.DEFAULT_MESSAGE_PIPELINE, payload, replyHandler -> {
                if (replyHandler.succeeded()) {
                } else {
                    System.out.println("could not push: " + payload + " reason: " + replyHandler.cause().getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * task completion
     * @param id
     * @param completionHandler
     */
    public void completeTask(String id, KafkaTaskResponseHandler completionHandler) {
        config.kafkaConsumer().subscribe(id, subscribe -> {
            if (subscribe.succeeded()) {

                //handler
                config.kafkaConsumer().handler(record -> {
                    EventState result = completionHandler.handle(record.value());
                    persistState(id, result, record.value());

                    //replace with kafka
                    config.kafkaProducer().write(KafkaProducerRecord.create(record.topic() + RESULT_POSTFIX, record.key(), result.name()), handler -> {
                        if (handler.succeeded()) {
                            System.out.println("sending completion to: " + record.topic() + RESULT_POSTFIX);
                        }
                    });
                });
            }
        });
    }

    private void persistState(String id, EventState result, String value) {
        System.out.println(String.format("persisting.. %s, %s, %s", id, result.name(), value));
    }
}
