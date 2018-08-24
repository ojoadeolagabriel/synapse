package com.synapse.task.context

enum EventState {
    Success(1),
    Failed(0),
    Retry(2),
    Pending(3)

    private int value
    int getValue(){
        return value
    }
    EventState(int val){
        value = val
    }
}
