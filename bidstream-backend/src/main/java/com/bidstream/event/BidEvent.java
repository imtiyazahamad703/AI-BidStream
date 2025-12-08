package com.bidstream.event;

import java.time.LocalDateTime;

public class BidEvent {

    private Long auctionId;
    private String bidderEmail;
    private Double amount;
    private LocalDateTime timestamp;

    public BidEvent() {
    }

    public BidEvent(Long auctionId, String bidderEmail, Double amount, LocalDateTime timestamp) {
        this.auctionId = auctionId;
        this.bidderEmail = bidderEmail;
        this.amount = amount;
        this.timestamp = timestamp;
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
