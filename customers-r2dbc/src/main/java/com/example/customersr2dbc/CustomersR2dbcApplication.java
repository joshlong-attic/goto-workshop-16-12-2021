package com.example.customersr2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@EnableTransactionManagement
@SpringBootApplication
public class CustomersR2dbcApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CustomersR2dbcApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerService service, CustomerRepository repository) {
        return args -> {
            service
                    .saveAll("josh@joshlong.com", "test@test.com")
                    .onErrorResume(ex -> Flux.empty())
                    .blockLast();
            System.out.println("waiting..");
            repository.findAll().subscribe(System.out::println);

        };
    }


    @Bean
    RouterFunction<ServerResponse> routes(CustomerRepository repository) {
        return route()
                .GET("/customers", request -> ok().body(repository.findAll(), Customer.class)).build();
    }
}


@Service
class CustomerService {

    private final CustomerRepository repository;

    CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Flux<Customer> saveAll(String... names) {
        return Flux.just(names)
                .map(n -> new Customer(null, n))
                .flatMap(this.repository::save)
                .doOnNext(customer -> Assert.isTrue(customer.email().contains("@"), "the email must contain a '@' in it!"));
    }

}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String email) {
}
