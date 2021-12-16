package com.example.rsocketservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@SpringBootApplication
public class RsocketServiceApplication {

	@MessageMapping ("hello.{name}")
	Mono<String> hello (@DestinationVariable String name) {
		return Mono.just("Hello, " + name + "!") ;
	}

	public static void main(String[] args) {
		SpringApplication.run(RsocketServiceApplication.class, args);
	}

}
