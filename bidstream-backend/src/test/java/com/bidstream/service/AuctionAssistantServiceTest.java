package com.bidstream.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuctionAssistantServiceTest {

    @Autowired
    private AuctionAssistantService auctionAssistantService;

    @MockBean
    private GeminiChatService geminiChatService;

    @Test
    void generatePrompt_IncludesQuestionAndContext() {
        String question = "What is the starting bid?";
        String context = "The starting bid is $500.";
        
        String prompt = auctionAssistantService.generatePrompt(question, context);
        
        assertNotNull(prompt);
        assertTrue(prompt.contains(question));
        assertTrue(prompt.contains(context));
    }

    @Test
    void askAssistant_DelegatesToGeminiService() {
        when(geminiChatService.generateChatResponse(anyString())).thenReturn("AI Response");

        String response = auctionAssistantService.askAssistant("Q", "C");
        
        assertNotNull(response);
        assertTrue(response.contains("AI Response"));
    }
}
