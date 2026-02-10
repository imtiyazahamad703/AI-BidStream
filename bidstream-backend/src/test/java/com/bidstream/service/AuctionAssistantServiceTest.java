package com.bidstream.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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
    
    @Test
    void handleConversationTurn_IntegratesServicesAndSavesHistory() {
        VectorSearchService mockVectorService = org.mockito.Mockito.mock(VectorSearchService.class);
        ChatHistoryService mockHistoryService = org.mockito.Mockito.mock(ChatHistoryService.class);
        
        when(mockVectorService.retrieveContext(1L, "Q", 3)).thenReturn("Context");
        when(mockHistoryService.getHistory(1L)).thenReturn(java.util.Collections.emptyList());
        when(geminiChatService.generateChatResponse(anyString())).thenReturn("AI Response");
        
        String response = auctionAssistantService.handleConversationTurn(1L, 100L, "Q", mockVectorService, mockHistoryService);
        
        assertNotNull(response);
        assertEquals("AI Response", response);
        verify(mockHistoryService).saveMessage(1L, 100L, "USER", "Q");
        verify(mockHistoryService).saveMessage(1L, 100L, "ASSISTANT", "AI Response");
    }
}
