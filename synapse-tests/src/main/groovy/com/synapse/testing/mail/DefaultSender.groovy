package com.synapse.testing.mail

import org.springframework.stereotype.Component

@Component
class DefaultSender implements MailSender{
    @Override
    void send(String address, String body) {
        System.out.println("using DefaultSender")
    }
}
