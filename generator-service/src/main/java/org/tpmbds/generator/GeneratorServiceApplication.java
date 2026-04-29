package org.tpmbds.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GeneratorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneratorServiceApplication.class, args);
    }
}
