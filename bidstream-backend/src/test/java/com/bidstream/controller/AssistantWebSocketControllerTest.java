package com.bidstream.controller;

import com.bidstream.service.AuctionAssistantService;
import com.bidstream.service.VectorSearchService;
import com.bidstream.service.ChatHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssistantWebSocketControllerTest {

    @Test
    void handleAuctionQuestion_PublishesResponse() {
        AuctionAssistantService auctionAssistantService = mock(AuctionAssistantService.class);
        VectorSearchService vectorSearchService = mock(VectorSearchService.class);
        ChatHistoryService chatHistoryService = mock(ChatHistoryService.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);

        AssistantWebSocketController controller = new AssistantWebSocketController(
                auctionAssistantService, vectorSearchService, chatHistoryService, messagingTemplate);

        when(auctionAssistantService.handleConversationTurn(anyLong(), anyLong(), anyString(), any(), any()))
                .thenReturn("AI Response");

        controller.handleAuctionQuestion(1L, Map.of("question", "Hello", "userId", 100L));

        verify(messagingTemplate).convertAndSend(eq("/topic/auction/1/assistant"), any(Map.class));
    }
    
    @Test
    void endToEndAuctionAssistantWorkflow_CompletesFullCycle() {
        AuctionAssistantService auctionAssistantService = mock(AuctionAssistantService.class);
        VectorSearchService vectorSearchService = mock(VectorSearchService.class);
        ChatHistoryService chatHistoryService = mock(ChatHistoryService.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);

        AssistantWebSocketController controller = new AssistantWebSocketController(
                auctionAssistantService, vectorSearchService, chatHistoryService, messagingTemplate);

        when(auctionAssistantService.handleConversationTurn(anyLong(), anyLong(), anyString(), any(), any()))
                .thenReturn("Full Workflow AI Response");

        controller.handleAuctionQuestion(99L, Map.of("question", "What is the end time?", "userId", 50L));

        verify(auctionAssistantService).handleConversationTurn(eq(99L), eq(50L), eq("What is the end time?"), eq(vectorSearchService), eq(chatHistoryService));
        verify(messagingTemplate).convertAndSend(eq("/topic/auction/99/assistant"), any(Map.class));
    }
}
