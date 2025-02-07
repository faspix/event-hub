package com.faspix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CompilationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompilationApplication.class, args);
    }
}