package com.synapse.testing.mail

import org.springframework.stereotype.Component

@Component
class PrettySender implements MailSender{
    @Override
    void send(String address, String body) {
        System.out.println("using PrettySender")
    }
}
