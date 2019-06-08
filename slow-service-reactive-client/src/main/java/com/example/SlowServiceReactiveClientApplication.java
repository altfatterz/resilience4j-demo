package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.commons.ReactiveCircuitBreaker;
import org.springframework.cloud.circuitbreaker.commons.ReactiveCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SlowServiceReactiveClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlowServiceReactiveClientApplication.class, args);
    }


}

@RestController
class ClientController {

    private final WebClient.Builder webClient;
    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    public ClientController(WebClient.Builder webClient,
                            ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = webClient;
        this.reactiveCircuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/")
    public Mono<String> hello() {
        return webClient.build()
                .get().uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("slow-service").path("/slow")
                        .build())
                .retrieve().bodyToMono(String.class)
                .transform(it -> {
                    ReactiveCircuitBreaker rcb = reactiveCircuitBreakerFactory.create("slow");
                    return rcb.run(it, throwable -> Mono.just("fallback"));
                });
    }
}

