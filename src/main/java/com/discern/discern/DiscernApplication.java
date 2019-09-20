package com.discern.discern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class DiscernApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscernApplication.class, args);
    }
}
