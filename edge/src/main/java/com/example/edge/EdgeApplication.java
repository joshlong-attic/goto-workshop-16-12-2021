package com.example.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class EdgeApplication {

    @Bean
    SecurityWebFilterChain authorization(ServerHttpSecurity http) {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ae -> ae
                        .pathMatchers("/proxy").authenticated()
                        .anyExchange().permitAll()
                )
                .build();
    }

    @Bean
    MapReactiveUserDetailsService authentication() {
        return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
                .username("jlong")
                .password("pw")
                .roles("USER")
                .build());
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(5, 7);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder rlb, RedisRateLimiter redisRateLimiter) {
        return rlb.routes()
//                .route()
                .route(rs -> rs.path("/proxy").and().host("*.spring.io")
                        .filters(fs -> fs.setPath("/customers")
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                                .retry(10)
                                .requestRateLimiter(rl -> rl
                                        .setRateLimiter(redisRateLimiter)
                                )
                        ).uri("http://localhost:8080/")).build();
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(EdgeApplication.class, args);
    }

}

record Customer(Integer id, String email) {
}

record Profile(Integer id, Integer customerId) {
}

record CustomerProfile(Customer customer, Profile profile) {
}

@Controller
record CrmGraphqlController(CrmClient crmClient) {


    @BatchMapping
    Mono<Map<Customer, Profile>> profile(List<Customer> customer) {
        var integerStream = customer.stream().map(Customer::id);
        var ids = integerStream.toList();
        return this.crmClient
                .getProfilesById(ids)
                .collectList()
                .map(list -> {
                    var map = new HashMap<Customer, Profile>();
                    for (var p : list) {
                        var  customerStream = customer
                                .stream().filter(c -> c.id().equals(p.customerId())).toList();
                        map.put(customerStream.get(0), p);
                    }
                    return map;
                });


    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.crmClient.getCustomers();
    }

}

@Controller
@ResponseBody
record CrmRestController(CrmClient crmClient) {

    @GetMapping("/cos")
    Flux<CustomerProfile> getCustomerProfiles() {
        return this.crmClient.getCustomerProfiles();
    }
}

@Component
record CrmClient(WebClient http) {


    Flux<CustomerProfile> getCustomerProfiles() {
        return this.getCustomers()
                .flatMap(customer -> Mono.zip(Mono.just(customer), getProfileFor(customer.id())))
                .map(tuple2 -> new CustomerProfile(tuple2.getT1(), tuple2.getT2()))
                .retryWhen(Retry.backoff(10, Duration.ofSeconds(1)))
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(ex -> Flux.empty());
    }

    Flux<Profile> getProfilesById(Collection<Integer> ids) {
        String collect = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        return this.http.get().uri("http://localhost:8181/profiles/{id}", collect)
                .retrieve().bodyToFlux(Profile.class);
    }

    Mono<Profile> getProfileFor(Integer customerId) {
        return this.http.get().uri("http://localhost:8181/profile/{id}", customerId)
                .retrieve()
                .bodyToMono(Profile.class)
                .retryWhen(Retry.backoff(10, Duration.ofSeconds(1)))
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(ex -> Mono.empty())
                ;
    }

    Flux<Customer> getCustomers() {
        return this.http.get().uri("http://localhost:8080/customers").retrieve().bodyToFlux(Customer.class);
    }
}