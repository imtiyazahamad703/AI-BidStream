package com.bidstream.controller;

import com.bidstream.service.AuctionAssistantService;
import com.bidstream.service.VectorSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final AuctionAssistantService auctionAssistantService;
    private final VectorSearchService vectorSearchService;

    public ChatController(AuctionAssistantService auctionAssistantService, VectorSearchService vectorSearchService) {
        this.auctionAssistantService = auctionAssistantService;
        this.vectorSearchService = vectorSearchService;
    }

    @PostMapping("/auction/{auctionId}")
    public ResponseEntity<?> askAuctionQuestion(
            @PathVariable Long auctionId,
            @RequestBody Map<String, String> request) {
        
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Question is required"));
        }
        
        String response = auctionAssistantService.askAuctionAssistant(auctionId, question, vectorSearchService);
        
        return ResponseEntity.ok(Map.of(
                "auctionId", auctionId,
                "question", question,
                "response", response
        ));
    }
}
