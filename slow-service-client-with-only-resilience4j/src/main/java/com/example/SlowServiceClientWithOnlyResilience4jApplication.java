package com.example;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SlowServiceClientWithOnlyResilience4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlowServiceClientWithOnlyResilience4jApplication.class, args);
    }

}

@RestController
class ClientController {

    private final SlowServiceClient slowServiceClient;

    public ClientController(SlowServiceClient slowServiceClient) {
        this.slowServiceClient = slowServiceClient;
    }

    @GetMapping("/")
    public Mono<String> hello() {
        return slowServiceClient.getValue();
    }

}

@Component
class SlowServiceClient {

    private final WebClient.Builder webClient;

    public SlowServiceClient(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "hello", fallbackMethod = "fallback")
    public Mono<String> getValue() {
        return webClient.build()
                .get().uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("slow-service").path("/slow")
                        .build())
                .retrieve().bodyToMono(String.class);
    }

    public Mono<String> fallback(Throwable throwable) {
        return Mono.just("Recovered from: " + throwable.getMessage());
    }

}