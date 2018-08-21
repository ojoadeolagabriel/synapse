package com.synapse.config.server

import spock.lang.Specification

class NumberSpec extends Specification{
    def "can add"(){
        given:
            int a = 0
        expect:
            a == 0
    }
}
