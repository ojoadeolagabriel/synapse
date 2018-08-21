package com.synapse.testing

import com.synapse.testing.mail.MailSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class App {
    static void main(String[] args) {
        SpringApplication.run(App, args)
    }

    @Autowired
    void initImageLoader(List<MailSender> senderList){
        if(senderList?.size() == 0)
            throw new RuntimeException("invalid [senderlist] size")
        for(MailSender sender : senderList){
            sender.send("a@ycom", "body")
        }
    }
}

