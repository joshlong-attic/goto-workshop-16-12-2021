package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.context.ReactiveWebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
@ResponseBody
@SpringBootApplication
public class ServiceApplication {

    private final AtomicInteger port = new AtomicInteger();

    @EventListener
    public void webServerReady(ReactiveWebServerInitializedEvent event) {
        WebServer webServer = event.getWebServer();
        this.port.set(webServer.getPort());
        System.out.println("the port is " + this.port.get());
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @GetMapping("/hello")
    String hello() {
        return "Hello, world from port " + this.port.get();
    }
}


