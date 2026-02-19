package com.bidstream.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class AiAssistantConfig {

    @PostConstruct
    public void init() {
        System.out.println("AI Assistant Backend Pipeline (RAG + WebSocket + Caching) Initialized successfully.");
    }
}
