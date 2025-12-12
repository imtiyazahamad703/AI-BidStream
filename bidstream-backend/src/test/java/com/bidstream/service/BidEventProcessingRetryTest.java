package com.bidstream.service;

import com.bidstream.entity.Bid;
import com.bidstream.event.BidEvent;
import com.bidstream.repository.jpa.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BidEventProcessingRetryTest {

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
    void consumeBidEvent_OnFailure_ThrowsExceptionForRetry() {
        BidEvent event = new BidEvent(1L, "bidder@test.com", 150.0, LocalDateTime.now());
        
        // Simulate DB failure
        when(bidRepository.save(any(Bid.class))).thenThrow(new RuntimeException("DB Connection failed"));

        assertThrows(RuntimeException.class, () -> bidEventConsumer.consumeBidEvent(event));
        
        // Note: Actual retry behavior is tested via Spring Context in integration tests.
        // This validates that the consumer throws the exception to allow DefaultErrorHandler to catch it.
        verify(bidRepository, times(1)).save(any(Bid.class));
    }
}
