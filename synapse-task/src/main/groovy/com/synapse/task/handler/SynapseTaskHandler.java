package com.synapse.task.handler;

import com.synapse.task.event.Event;

public interface SynapseTaskHandler {
    Event handle();
}
