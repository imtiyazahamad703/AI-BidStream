package com.bidstream.event;

import java.time.LocalDateTime;

public class BidEvent {

    private Long auctionId;
    private String bidderEmail;
    private Double amount;
    private LocalDateTime timestamp;
    private String trackingId;

    public BidEvent() {
    }

    public BidEvent(Long auctionId, String bidderEmail, Double amount, LocalDateTime timestamp, String trackingId) {
        this.auctionId = auctionId;
        this.bidderEmail = bidderEmail;
        this.amount = amount;
        this.timestamp = timestamp;
        this.trackingId = trackingId;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getBidderEmail() {
        return bidderEmail;
    }

    public void setBidderEmail(String bidderEmail) {
        this.bidderEmail = bidderEmail;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    @Override
    public String toString() {
        return "BidEvent{" +
                "auctionId=" + auctionId +
                ", bidderEmail='" + bidderEmail + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
