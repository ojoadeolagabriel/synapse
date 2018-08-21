package com.synapse.io.config.client;

import com.synapse.io.util.CustomStringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        System.out.println(new CustomStringUtil().toUpper("dexter"));
        SpringApplication.run(App.class, args);
    }
}
