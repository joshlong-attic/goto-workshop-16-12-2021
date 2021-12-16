package com.example.configclient;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@SpringBootApplication
public class ConfigClientApplication {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    ConfigClientApplication(Environment environment) {
        this.environment = environment;
    }


    @GetMapping("/message")
    String read() {
        return this.environment.getProperty("message");
    }


}
