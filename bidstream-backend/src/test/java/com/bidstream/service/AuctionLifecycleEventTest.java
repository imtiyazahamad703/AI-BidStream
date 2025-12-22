package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuctionLifecycleEventTest {

    private AuctionRepository auctionRepository;
    private AuctionEventPublisher auctionEventPublisher;
    private BidService bidService;
    private AuctionLifecycleService auctionLifecycleService;

    @BeforeEach
    void setUp() {
        auctionRepository = Mockito.mock(AuctionRepository.class);
        auctionEventPublisher = Mockito.mock(AuctionEventPublisher.class);
        bidService = Mockito.mock(BidService.class);
        
        auctionLifecycleService = new AuctionLifecycleService(auctionRepository, auctionEventPublisher, bidService);
    }

    @Test
    void testStartScheduledAuctions() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(com.bidstream.entity.AuctionStatus.SCHEDULED);
        
        when(auctionRepository.findByStatusAndStartTimeBefore(
                eq(com.bidstream.entity.AuctionStatus.SCHEDULED), any(LocalDateTime.class)))
                .thenReturn(List.of(auction));
                
        auctionLifecycleService.startScheduledAuctions();
        
        verify(auctionRepository).save(auction);
        verify(auctionEventPublisher).publishAuctionStarted(1L);
    }

    @Test
    void testEndCompletedAuctions() {
        Auction auction = new Auction();
        auction.setId(2L);
        auction.setStatus(com.bidstream.entity.AuctionStatus.ACTIVE);
        
        when(auctionRepository.findByStatusAndEndTimeBefore(
                eq(com.bidstream.entity.AuctionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(List.of(auction));
                
        when(bidService.getHighestBid(2L)).thenReturn(500.0);
        
        auctionLifecycleService.endCompletedAuctions();
        
        verify(auctionRepository).save(auction);
        verify(auctionEventPublisher).publishAuctionEnded(2L, "winner@placeholder.com", 500.0);
    }
}
