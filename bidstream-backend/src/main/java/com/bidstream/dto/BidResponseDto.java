package com.bidstream.dto;

import com.bidstream.entity.BidStatus;
import java.time.LocalDateTime;

public class BidResponseDto {
    private Long id;
    private Long auctionId;
    private String bidderEmail;
    private Double amount;
    private LocalDateTime createdAt;
    private BidStatus status;
    private String trackingId;

    public BidResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
    
    public String getBidderEmail() { return bidderEmail; }
    public void setBidderEmail(String bidderEmail) { this.bidderEmail = bidderEmail; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public BidStatus getStatus() { return status; }
    public void setStatus(BidStatus status) { this.status = status; }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
}
