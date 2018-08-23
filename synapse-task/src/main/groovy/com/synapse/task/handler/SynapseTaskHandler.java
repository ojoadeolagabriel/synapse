package com.synapse.task.handler;

import com.synapse.task.event.SynapseEvent;

public interface SynapseTaskHandler {
    SynapseEvent handle();
}
