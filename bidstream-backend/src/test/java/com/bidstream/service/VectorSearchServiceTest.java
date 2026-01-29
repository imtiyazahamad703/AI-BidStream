package com.bidstream.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class VectorSearchServiceTest {

    @Autowired
    private VectorSearchService vectorSearchService;

    @MockBean
    private MongoTemplate mongoTemplate;

    @Test
    void searchSimilarDocuments_ReturnsResults() {
        // Simple test to ensure the service loads and method can be called
        // Since MongoTemplate is mocked, it will return an empty list by default
        var results = vectorSearchService.searchSimilarDocuments(1L, "test query", 5);
        assertNotNull(results);
    }
    
    @Test
    void searchSimilarDocuments_WithItemScope_ReturnsResults() {
        var results = vectorSearchService.searchSimilarDocuments(1L, 100L, "test query", 5);
        assertNotNull(results);
    }
}
