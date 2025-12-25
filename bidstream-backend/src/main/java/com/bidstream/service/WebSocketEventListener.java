package com.bidstream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    private final ParticipantTrackerService participantTrackerService;
    private final AuctionEventPublisher auctionEventPublisher;

    public WebSocketEventListener(ParticipantTrackerService participantTrackerService, AuctionEventPublisher auctionEventPublisher) {
        this.participantTrackerService = participantTrackerService;
        this.auctionEventPublisher = auctionEventPublisher;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("Web socket connection disconnected: {}", sessionId);
        
        participantTrackerService.removeParticipant(sessionId);
    }
    
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        logger.info("New subscription to destination: {}", destination);
        
        if (destination != null && destination.startsWith("/topic/auction.")) {
            try {
                Long auctionId = Long.parseLong(destination.substring("/topic/auction.".length()));
                participantTrackerService.addParticipant(auctionId, sessionId);
                
                // Publish active count
                int count = participantTrackerService.getActiveBidderCount(auctionId);
                auctionEventPublisher.publishEvent(new com.bidstream.event.AuctionEvent(
                    auctionId,
                    com.bidstream.event.AuctionEvent.EventType.AUCTION_STARTED, // Or a new event type PARTICIPANT_COUNT
                    java.util.Map.of("activeBidders", count)
                ));
            } catch (NumberFormatException e) {
                logger.warn("Invalid auction ID in destination: {}", destination);
            }
        }
    }
}
