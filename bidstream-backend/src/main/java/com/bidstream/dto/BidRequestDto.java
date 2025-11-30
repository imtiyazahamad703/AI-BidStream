package com.bidstream.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BidRequestDto {
    
    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be positive")
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
