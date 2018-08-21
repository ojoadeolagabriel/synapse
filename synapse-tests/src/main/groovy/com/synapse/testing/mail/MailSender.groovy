package com.synapse.testing.mail

interface MailSender {
    void send(String address, String body)
}