package com.bidstream.dto;

import com.bidstream.entity.AuctionStatus;
import java.time.LocalDateTime;

public class AuctionResponseDto {
    private Long id;
    private String itemId;
    private String sellerEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private Double currentHighestBid;
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public AuctionStatus getStatus() { return status; }
    public void setStatus(AuctionStatus status) { this.status = status; }
    public Double getCurrentHighestBid() { return currentHighestBid; }
    public void setCurrentHighestBid(Double currentHighestBid) { this.currentHighestBid = currentHighestBid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
