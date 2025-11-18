package com.bidstream.entity;

public enum AuctionStatus {
    SCHEDULED, // Not started yet, but scheduled for the future
    ACTIVE,    // Currently accepting bids
    COMPLETED, // Reached end time
    CANCELLED  // Cancelled by seller or admin
}
