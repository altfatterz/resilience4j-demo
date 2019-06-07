package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.commons.CircuitBreaker;
import org.springframework.cloud.circuitbreaker.commons.CircuitBreakerFactory;
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
    private final CircuitBreakerFactory circuitBreakerFactory;

    public ClientController(WebClient.Builder webClient,
                            CircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = webClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @GetMapping("/")
    public Mono<String> hello() {
        return webClient.build()
                .get().uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("slow-service").path("/slow")
                        .build())
                .retrieve().bodyToMono(String.class).transform(it -> {
                    CircuitBreaker cb = circuitBreakerFactory.create("slow");
                    return cb.run(() -> it, throwable -> Mono.just("fallback"));
                });
    }
}

