package com.synapse.task.event;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SynapseEvent {
    private String key;
    private String topic;
    private String message;
    private String partition;
    private Map<String, String> headers = new HashMap<>();
    public void putHeader(String key, String value){
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
