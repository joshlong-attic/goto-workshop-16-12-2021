package com.example.dcconsumer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;


@SpringBootApplication
public class DcConsumerApplication {

    @Bean
    RouteLocator gateway (RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(rs -> rs.path("/proxy")
                        .uri("lb://service/hello")
                )
                .build() ;
    }

    @Bean
    @LoadBalanced
    WebClient.Builder builder() {
        return WebClient.builder();
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ApplicationRunner runner(WebClient http, RestTemplate restTemplate,
//                             ReactiveDiscoveryClient reactiveDiscoveryClient,
                             DiscoveryClient discoveryClient) {
        return args -> {

            List<ServiceInstance> service = discoveryClient.getInstances("service");
            service.forEach(si -> System.out.println(si.getHost() + ':' + si.getPort()));

            for (var i = 0; i < 10; i++) {
                /*http.get().uri("http://service/hello").retrieve()
                        .bodyToMono(String.class)
                        .subscribe(System.out::println);*/

                ResponseEntity<String> forEntity = restTemplate.getForEntity("http://service/hello", String.class);
                System.out.println(forEntity.getBody());
            }

            Mono<String> timeout = http.get().uri("....")
                    .retrieve().bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(1))
                    .retry();


            Flux<ServiceInstance> serviceInstanceFlux = Flux.fromIterable(service);
            Flux<String> possibleResults = serviceInstanceFlux
                    .flatMap(si -> http.get()
                            .uri("http://" + si.getHost() + ':' + si.getPort() + "/hello").retrieve()
                            .bodyToMono(String.class));
            Flux<String> stringFlux = Flux.firstWithSignal(possibleResults);

            stringFlux.subscribe();


        };
    }

    public static void main(String[] args) {
        SpringApplication.run(DcConsumerApplication.class, args);
    }

}
