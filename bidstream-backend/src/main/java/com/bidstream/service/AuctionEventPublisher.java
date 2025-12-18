package com.bidstream.service;

import com.bidstream.event.AuctionEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuctionEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public AuctionEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishEvent(AuctionEvent event) {
        String destination = "/topic/auction." + event.getAuctionId();
        messagingTemplate.convertAndSend(destination, event);
    }

    public void publishBidPlaced(Long auctionId, Double amount, String bidderEmail) {
        Map<String, Object> payload = Map.of(
            "amount", amount,
            "bidder", bidderEmail
        );
        publishEvent(new AuctionEvent(auctionId, AuctionEvent.EventType.BID_PLACED, payload));
    }
    
    public void publishAuctionStarted(Long auctionId) {
        publishEvent(new AuctionEvent(auctionId, AuctionEvent.EventType.AUCTION_STARTED, Map.of()));
    }
    
    public void publishAuctionEnded(Long auctionId, String winnerEmail, Double finalAmount) {
        Map<String, Object> payload = Map.of(
            "winner", winnerEmail != null ? winnerEmail : "",
            "finalAmount", finalAmount != null ? finalAmount : 0.0
        );
        publishEvent(new AuctionEvent(auctionId, AuctionEvent.EventType.AUCTION_ENDED, payload));
    }
}
