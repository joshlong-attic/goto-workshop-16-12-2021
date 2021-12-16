package com.example.customersr2dbc;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Collection;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomersR2dbcApplicationTests {

	@Autowired
	WebTestClient webTestClient ;



	@Test
	void contextLoads() {
		this.webTestClient.get()
				.uri("http://localhost:8080/customers")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody().jsonPath("@.[0].id")  .exists() ;

			 ;
	}

}
