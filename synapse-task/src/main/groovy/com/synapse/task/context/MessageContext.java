package com.synapse.task.context;

import com.synapse.task.config.KafkaSynapseConfig;
import com.synapse.task.event.SynapseEvent;
import com.synapse.task.handler.SynapseTaskHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

public class MessageContext extends AbstractVerticle {

    SynapseTaskHandler handle;
    private KafkaSynapseConfig config;
    public SynapseEvent synapseEvent;

    public MessageContext(SynapseTaskHandler handler) {
        this.handle = handler;
    }

    public void start() {
        synapseEvent = handle.handle();
        processEvent(synapseEvent);
    }

    private void processEvent(SynapseEvent event) {
        if (event != null) {
            String key = event.getKey();
            String topic = event.getTopic();
            String message = event.getMessage();

            config.kafkaProducer().write(KafkaProducerRecord.create(topic, key, message), handler -> {
                if(handler.succeeded()){
                    System.out.println("Successfully written message to topic: " + topic + " key: " + key + " payload: " + message);
                }
            });
        }
    }

    public void stop() {

    }

    public void setConfig(KafkaSynapseConfig config) {
        this.config = config;
    }
}
