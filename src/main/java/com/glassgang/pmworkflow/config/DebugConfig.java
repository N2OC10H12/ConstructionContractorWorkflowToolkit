package com.glassgang.pmworkflow.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebugConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @PostConstruct
    public void print() {
        System.out.println("=== DATASOURCE URL ===");
        System.out.println(url);
        System.out.println("======================");
    }
}