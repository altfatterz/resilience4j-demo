package com.example;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.circuitbreaker.commons.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SlowServiceClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlowServiceClientApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


}

@RestController
class ClientController {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final Timer slowServiceTimer;

    public ClientController(RestTemplate restTemplate,
                            CircuitBreakerFactory circuitBreakerFactory,
                            MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.slowServiceTimer = meterRegistry.timer("slow.service");
    }

    @GetMapping("/")
    public String hello() {
        return slowServiceTimer.record(() ->
                circuitBreakerFactory.create("slow.service").run(
                        () -> restTemplate.getForObject("http://slow-service/slow", String.class),
                        throwable -> "fallback"));
    }

}