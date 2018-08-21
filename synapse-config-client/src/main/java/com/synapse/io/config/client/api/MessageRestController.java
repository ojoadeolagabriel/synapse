package com.synapse.io.config.client.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class MessageRestController {
    @Value("${message:hello default}")
    String message;

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    String getMessage() {
        return message;
    }
}
