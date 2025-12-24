package com.bidstream.service;

import com.bidstream.entity.Bid;
import com.bidstream.event.BidEvent;
import com.bidstream.repository.jpa.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BidderNotificationEventTest {

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
    void testOutbidNotificationSent() {
        Bid previousBid = new Bid();
        previousBid.setBidderEmail("user1@test.com");
        
        when(bidRepository.findTopByAuctionIdOrderByAmountDesc(1L)).thenReturn(previousBid);
        
        BidEvent event = new BidEvent(1L, "user2@test.com", 300.0, LocalDateTime.now(), "track-123");
        
        bidEventConsumer.consumeBidEvent(event);
        
        verify(auctionEventPublisher).publishOutbidNotification(eq(1L), eq("user1@test.com"), eq(300.0));
    }

    @Test
    void testNoOutbidNotificationIfSameUser() {
        Bid previousBid = new Bid();
        previousBid.setBidderEmail("user1@test.com");
        
        when(bidRepository.findTopByAuctionIdOrderByAmountDesc(1L)).thenReturn(previousBid);
        
        BidEvent event = new BidEvent(1L, "user1@test.com", 350.0, LocalDateTime.now(), "track-123");
        
        bidEventConsumer.consumeBidEvent(event);
        
        verify(auctionEventPublisher, never()).publishOutbidNotification(anyLong(), anyString(), anyDouble());
    }
}
