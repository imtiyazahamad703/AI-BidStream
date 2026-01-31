package com.bidstream.service;

import org.springframework.stereotype.Service;

@Service
public class AuctionAssistantService {

    private final GeminiChatService geminiChatService;

    public AuctionAssistantService(GeminiChatService geminiChatService) {
        this.geminiChatService = geminiChatService;
    }

    public String generatePrompt(String userQuestion, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert auction assistant for BidStream.\n");
        prompt.append("Use the following document context to answer the user's question.\n");
        prompt.append("Context:\n").append(context).append("\n\n");
        prompt.append("User Question: ").append(userQuestion).append("\n");
        
        return prompt.toString();
    }
}
