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

class LiveBidBroadcastingTest {

    private BidRepository bidRepository;
    private RedisBidCacheService redisBidCacheService;
    private AuctionEventPublisher auctionEventPublisher;
    private BidEventConsumer bidEventConsumer;

    @BeforeEach
    void setUp() {
        bidRepository = Mockito.mock(BidRepository.class);
        redisBidCacheService = Mockito.mock(RedisBidCacheService.class);
        auctionEventPublisher = Mockito.mock(AuctionEventPublisher.class);
        
        bidEventConsumer = new BidEventConsumer(bidRepository, redisBidCacheService, auctionEventPublisher);
    }

    @Test
    void testBidEventBroadcastedAfterPersistence() {
        BidEvent event = new BidEvent(1L, "bidder@test.com", 200.0, LocalDateTime.now(), "track-456");
        
        bidEventConsumer.consumeBidEvent(event);
        
        verify(auctionEventPublisher).publishBidPlaced(eq(1L), eq(200.0), eq("bidder@test.com"), any(java.time.LocalDateTime.class));
    }
}
