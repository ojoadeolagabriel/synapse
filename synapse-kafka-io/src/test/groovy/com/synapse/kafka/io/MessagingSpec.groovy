package com.synapse.kafka.io

import spock.lang.Specification

class MessagingSpec extends Specification{
    def setup(){

    }

    def "can message be sent"(){
        given:
            int a = 0
        expect:
            a == 0
    }
}
