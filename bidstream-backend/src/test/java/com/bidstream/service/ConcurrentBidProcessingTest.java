package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.repository.jpa.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ConcurrentBidProcessingTest {

    private BidRepository bidRepository;
    private AuctionService auctionService;
    private AuctionParticipationService participationService;
    private HighestBidService highestBidService;
    private RedisBidCacheService redisBidCacheService;
    private AuctionLockService lockService;
    private BidService bidService;

    @BeforeEach
    void setUp() {
        bidRepository = Mockito.mock(BidRepository.class);
        auctionService = Mockito.mock(AuctionService.class);
        participationService = Mockito.mock(AuctionParticipationService.class);
        highestBidService = Mockito.mock(HighestBidService.class);
        redisBidCacheService = Mockito.mock(RedisBidCacheService.class);
        lockService = Mockito.mock(AuctionLockService.class);

        bidService = new BidService(bidRepository, auctionService, participationService, 
                                    highestBidService, redisBidCacheService, lockService);
    }

    @Test
    void placeBid_WhenLockAcquisitionFails_ThrowsIllegalStateException() {
        when(lockService.tryLock(anyLong())).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, 
            () -> bidService.placeBid(1L, "bidder@test.com", 150.0));

        assertTrue(ex.getMessage().contains("High bid volume"));
        verify(lockService, never()).unlock(anyLong());
        verify(bidRepository, never()).save(any());
    }

    @Test
    void placeBid_WhenLockAcquired_ExecutesAndReleasesLock() {
        when(lockService.tryLock(1L)).thenReturn(true);
        
        Auction auction = new Auction();
        auction.setId(1L);
        when(auctionService.getAuctionById(1L)).thenReturn(Optional.of(auction));
        when(highestBidService.getCurrentHighestBid(1L)).thenReturn(Optional.of(100.0));
        
        bidService.placeBid(1L, "bidder@test.com", 150.0);

        verify(lockService).tryLock(1L);
        verify(bidRepository).save(any());
        verify(redisBidCacheService).updateHighestBid(1L, 150.0);
        verify(lockService).unlock(1L);
    }

    @Test
    void placeBid_WhenExceptionOccurs_StillReleasesLock() {
        when(lockService.tryLock(1L)).thenReturn(true);
        when(auctionService.getAuctionById(1L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, 
            () -> bidService.placeBid(1L, "bidder@test.com", 150.0));

        verify(lockService).tryLock(1L);
        verify(bidRepository, never()).save(any());
        verify(lockService).unlock(1L);
    }
}
