package com.bidstream.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GeminiEmbeddingService {

    @Value("${gemini.api.key:dummy_key}")
    private String apiKey;
    
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiEmbeddingService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Calls the Gemini API to generate embeddings for a given text chunk.
     */
    public List<Double> generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String url = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "model", "models/text-embedding-004",
            "content", Map.of("parts", List.of(Map.of("text", text)))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // Uncomment this when actually integrating with Gemini
            // Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            // return extractEmbeddingFromResponse(response);
            
            // For now, return a mock embedding to keep the pipeline functional without an actual key
            return mockEmbedding(768); 
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding from Gemini", e);
        }
    }

    private List<Double> extractEmbeddingFromResponse(Map<String, Object> response) {
        if (response != null && response.containsKey("embedding")) {
            Map<String, Object> embeddingNode = (Map<String, Object>) response.get("embedding");
            if (embeddingNode.containsKey("values")) {
                return (List<Double>) embeddingNode.get("values");
            }
        }
        return Collections.emptyList();
    }

    private List<Double> mockEmbedding(int size) {
        List<Double> mock = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mock.add(Math.random() - 0.5);
        }
        return mock;
    }
}
