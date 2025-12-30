package com.bidstream.service;

import com.bidstream.event.AuctionEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

@Service
public class AuctionEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public AuctionEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    public void publishEvent(AuctionEvent event) {
        String destination = "/topic/auction." + event.getAuctionId();
        messagingTemplate.convertAndSend(destination, event);
    }

    @Async
    public void publishBidPlaced(Long auctionId, Double amount, String bidderEmail, java.time.LocalDateTime timestamp) {
        Map<String, Object> payload = Map.of(
            "amount", amount,
            "bidder", bidderEmail != null ? bidderEmail : "Anonymous",
            "timestamp", timestamp != null ? timestamp.toString() : java.time.LocalDateTime.now().toString()
        );
        publishEvent(new AuctionEvent(auctionId, AuctionEvent.EventType.BID_PLACED, payload));
    }
    
    @Async
    public void publishAuctionStarted(Long auctionId) {
        publishEvent(new AuctionEvent(auctionId, AuctionEvent.EventType.AUCTION_STARTED, Map.of()));
    }
    
    @Async
    public void publishAuctionEnded(Long auctionId, String winnerEmail, Double finalAmount) {
        Map<String, Object> payload = Map.of(
            "winner", winnerEmail != null ? winnerEmail : "",
            "finalAmount", finalAmount != null ? finalAmount : 0.0
        );
        publishEvent(new AuctionEvent(auctionId, AuctionEvent.EventType.AUCTION_ENDED, payload));
    }

    @Async
    public void publishOutbidNotification(Long auctionId, String userEmail, Double newAmount) {
        Map<String, Object> payload = Map.of(
            "newAmount", newAmount,
            "message", "You have been outbid!"
        );
        // Using user-specific destination
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", 
            new AuctionEvent(auctionId, AuctionEvent.EventType.OUTBID, payload));
    }

    @Async
    public void publishBidRejectedNotification(Long auctionId, String userEmail, String reason) {
        Map<String, Object> payload = Map.of(
            "reason", reason != null ? reason : "Bid rejected",
            "message", "Your bid could not be processed"
        );
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/errors", 
            new AuctionEvent(auctionId, AuctionEvent.EventType.BID_REJECTED, payload));
    }
}
