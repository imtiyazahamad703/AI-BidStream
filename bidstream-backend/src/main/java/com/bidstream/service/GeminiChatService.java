package com.bidstream.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiChatService {

    @Value("${gemini.api.key:dummy_key}")
    private String apiKey;
    
    @Value("${gemini.api.chat.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiChatService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateChatResponse(String prompt) {
        String url = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
            ))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // Uncomment when actually integrating with Gemini
            // Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            // return extractTextFromResponse(response);
            
            // Mock response
            return "This is a simulated AI response based on the provided context."; 
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate chat response from Gemini", e);
        }
    }
}
