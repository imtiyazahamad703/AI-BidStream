package com.bidstream.event;

import java.time.LocalDateTime;
import java.util.Map;

public class AuctionEvent {
    
    public enum EventType {
        BID_PLACED,
        AUCTION_STARTED,
        AUCTION_ENDING,
        AUCTION_ENDED,
        BID_REJECTED,
        OUTBID,
        PARTICIPANT_COUNT
    }

    private Long auctionId;
    private EventType type;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;

    public AuctionEvent() {}

    public AuctionEvent(Long auctionId, EventType type, Map<String, Object> payload) {
        this.auctionId = auctionId;
        this.type = type;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
