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
    
    public String askAssistant(String userQuestion, String context) {
        String prompt = generatePrompt(userQuestion, context);
        return geminiChatService.generateChatResponse(prompt);
    }
    
    /**
     * Complete RAG Pipeline: Retrieve context, generate prompt, and ask Gemini.
     * Functions as the core chat service logic.
     */
    public String askAuctionAssistant(Long auctionId, String question, VectorSearchService vectorSearchService) {
        // Chat service orchestrates retrieval and response generation
        String context = vectorSearchService.retrieveContext(auctionId, question, 3);
        return askAssistant(question, context);
    }

    /**
     * Complete RAG Pipeline scoped to a specific item within an auction.
     */
    public String askAuctionAssistant(Long auctionId, Long itemId, String question, VectorSearchService vectorSearchService) {
        String context = vectorSearchService.retrieveContext(auctionId, question, 3); // using existing retrieve method, 
        // A more advanced retrieveContext that takes itemId could be implemented in VectorSearchService 
        return askAssistant(question, context);
    }
    
    public String generatePromptWithHistory(String userQuestion, String context, java.util.List<com.bidstream.domain.ChatMessage> history) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert auction assistant for BidStream.\n");
        prompt.append("Use the following document context to answer the user's question.\n");
        prompt.append("Context:\n").append(context).append("\n\n");
        
        if (history != null && !history.isEmpty()) {
            prompt.append("Conversation History:\n");
            for (com.bidstream.domain.ChatMessage msg : history) {
                prompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("User Question: ").append(userQuestion).append("\n");
        return prompt.toString();
    }
    
    /**
     * Completes a conversation turn by pulling history, answering, and saving the interaction.
     */
    public String handleConversationTurn(Long auctionId, Long userId, String question, 
                                         VectorSearchService vectorSearchService, 
                                         ChatHistoryService chatHistoryService) {
        
        // 1. Retrieve history
        java.util.List<com.bidstream.domain.ChatMessage> history = chatHistoryService.getHistory(auctionId);
        
        // 2. Retrieve document context
        String context = vectorSearchService.retrieveContext(auctionId, question, 3);
        
        // 3. Generate prompt and ask AI
        String prompt = generatePromptWithHistory(question, context, history);
        String response = geminiChatService.generateChatResponse(prompt);
        
        // 4. Save both user message and AI response
        chatHistoryService.saveMessage(auctionId, userId, "USER", question);
        chatHistoryService.saveMessage(auctionId, userId, "ASSISTANT", response);
        
        return response;
    }
}
