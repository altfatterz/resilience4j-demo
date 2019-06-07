package com.example;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.commons.Customizer;
import org.springframework.cloud.circuitbreaker.commons.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.ofDefaults;

@Configuration
public class CircuitBreakerConfig {

    // ReactiveCircuitBreakerFactory needed to be created otherwise
    // java.lang.NullPointerException: null
    //	at com.example.CircuitBreakerConfig.lambda$defaultCustomizer$1(CircuitBreakerConfig.java:26) ~[classes/:na]
    @Bean
    public ReactiveCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory() {
        return new ReactiveResilience4JCircuitBreakerFactory();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3)).build()).build());
    }
}
