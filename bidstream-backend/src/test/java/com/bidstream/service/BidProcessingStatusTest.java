package com.bidstream.service;

import com.bidstream.entity.Bid;
import com.bidstream.event.BidEvent;
import com.bidstream.repository.jpa.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class BidProcessingStatusTest {

    private BidRepository bidRepository;
    private RedisBidCacheService redisBidCacheService;
    private BidEventConsumer bidEventConsumer;

    @BeforeEach
    void setUp() {
        bidRepository = Mockito.mock(BidRepository.class);
        redisBidCacheService = Mockito.mock(RedisBidCacheService.class);
        
        bidEventConsumer = new BidEventConsumer(bidRepository, redisBidCacheService);
    }

    @Test
    void testStatusUpdatedToAcceptedOnSuccess() {
        BidEvent event = new BidEvent(1L, "bidder@test.com", 150.0, LocalDateTime.now(), "track-123");
        
        bidEventConsumer.consumeBidEvent(event);
        
        verify(redisBidCacheService).updateBidStatus("track-123", "ACCEPTED");
    }

    @Test
    void testStatusUpdatedToFailedOnError() {
        BidEvent event = new BidEvent(1L, "bidder@test.com", 150.0, LocalDateTime.now(), "track-123");
        
        Mockito.when(bidRepository.save(any(Bid.class))).thenThrow(new RuntimeException("DB Error"));
        
        try {
            bidEventConsumer.consumeBidEvent(event);
        } catch (Exception e) {
            // Expected
        }
        
        verify(redisBidCacheService).updateBidStatus("track-123", "FAILED");
    }
}
