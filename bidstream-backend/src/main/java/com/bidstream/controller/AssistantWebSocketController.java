package com.bidstream.controller;

import com.bidstream.service.AuctionAssistantService;
import com.bidstream.service.VectorSearchService;
import com.bidstream.service.ChatHistoryService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class AssistantWebSocketController {

    private final AuctionAssistantService auctionAssistantService;
    private final VectorSearchService vectorSearchService;
    private final ChatHistoryService chatHistoryService;
    private final SimpMessagingTemplate messagingTemplate;

    public AssistantWebSocketController(AuctionAssistantService auctionAssistantService,
                                        VectorSearchService vectorSearchService,
                                        ChatHistoryService chatHistoryService,
                                        SimpMessagingTemplate messagingTemplate) {
        this.auctionAssistantService = auctionAssistantService;
        this.vectorSearchService = vectorSearchService;
        this.chatHistoryService = chatHistoryService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/auction/{auctionId}/ask")
    public void handleAuctionQuestion(@DestinationVariable Long auctionId, @Payload Map<String, Object> payload) {
        String question = (String) payload.get("question");
        Long userId = payload.containsKey("userId") ? Long.valueOf(payload.get("userId").toString()) : 1L;

        try {
            // Process question via RAG Pipeline
            String response = auctionAssistantService.handleConversationTurn(auctionId, userId, question, vectorSearchService, chatHistoryService);

            // Publish success to topic
            messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/assistant", Map.of(
                    "type", "ASSISTANT_RESPONSE",
                    "question", question,
                    "response", response,
                    "auctionId", auctionId,
                    "status", "SUCCESS"
            ));
        } catch (Exception e) {
            // Publish error to topic
            messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/assistant", Map.of(
                    "type", "ASSISTANT_ERROR",
                    "question", question,
                    "error", e.getMessage(),
                    "auctionId", auctionId,
                    "status", "FAILED"
            ));
        }
    }
}
