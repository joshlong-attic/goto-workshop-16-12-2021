package com.example.consumer;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ConsumerApplication {

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    ApplicationRunner runner(WebClient webClient) {
        return args -> {
            Mono<String> stringMono = webClient.get().uri("http://localhost:8085/hello")
                    .retrieve()
                    .bodyToMono(String.class);
            stringMono.subscribe(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
