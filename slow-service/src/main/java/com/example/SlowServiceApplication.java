package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SlowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlowServiceApplication.class, args);
    }

    @GetMapping("/slow")
    public String slow() {
        return "Hello from slow-service";
    }
}
