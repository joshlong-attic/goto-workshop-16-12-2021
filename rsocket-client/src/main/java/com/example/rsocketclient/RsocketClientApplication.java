package com.example.rsocketclient;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;

@SpringBootApplication
public class RsocketClientApplication {

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.tcp("localhost", 8181);
    }

    @Bean
    ApplicationRunner applicationRunner(RSocketRequester rSocketRequester) {
        return args ->
                rSocketRequester
                        .route("hello.{name}", "Bob")
                        .retrieveMono(String.class)
                        .subscribe(System.out::println);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RsocketClientApplication.class, args);
        Thread.currentThread().join();
    }

}
