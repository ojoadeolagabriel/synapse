package com.synapse.task.handler;

import com.synapse.task.event.CompletionEvent;

public interface ProcessCompletionHandler {
    void handle(CompletionEvent body);
}
