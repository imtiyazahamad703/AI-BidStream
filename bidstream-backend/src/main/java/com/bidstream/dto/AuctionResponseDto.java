package com.bidstream.dto;

import com.bidstream.entity.AuctionStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AuctionResponseDto {
    private Long id;
    private String itemId;
    private String itemName;          // populated by controller for item info
    private String sellerEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private Double currentHighestBid;
    private Double startingPrice;     // initial bid threshold
    private LocalDateTime createdAt;

    // Computed timing metadata
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) return 0;
        return ChronoUnit.MINUTES.between(startTime, endTime);
    }

    public boolean isActive() {
        return AuctionStatus.ACTIVE.equals(status);
    }

    public boolean isScheduled() {
        return AuctionStatus.SCHEDULED.equals(status);
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
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
    public Double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(Double startingPrice) { this.startingPrice = startingPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
