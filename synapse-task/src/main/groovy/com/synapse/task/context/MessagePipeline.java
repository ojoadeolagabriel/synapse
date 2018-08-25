package com.synapse.task.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.event.SynapseEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.io.IOException;

public class MessagePipeline extends AbstractVerticle {

    private static ObjectMapper mapper = new ObjectMapper();
    private KafkaSynapseConfig config;

    public MessagePipeline(KafkaSynapseConfig config) {
        this.config = config;
    }

    public void start() {
        vertx.eventBus().consumer("::synapse.message.pipeline::", handler -> {
            Object objBody = handler.body();
            if (objBody != null) {
                try {
                    SynapseEvent event = mapper.readValue(objBody.toString(), SynapseEvent.class);
                    processEvent(event);
                    handler.reply("ok");
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.reply("not-ok: " + e.getMessage());
                }
            }
        });
    }

    private void processEvent(SynapseEvent event) {
        if (event != null) {
            String key = event.getKey();
            String topic = event.getTopic();
            String message = event.getMessage();

            config.kafkaProducer().write(KafkaProducerRecord.create(topic, key, message), handler -> {
                if (handler.succeeded()) {
                    System.out.println("Successfully written message to topic: " + topic + " key: " + key + " payload: " + message);
                }
            });
        }
    }

    public void stop() {

    }
}