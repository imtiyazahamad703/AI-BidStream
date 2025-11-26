package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuctionParticipationServiceTest {

    private AuctionParticipationService participationService;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        AuctionRepository auctionRepository = Mockito.mock(AuctionRepository.class);
        ItemService itemService = Mockito.mock(ItemService.class);
        auctionService = new AuctionService(auctionRepository, itemService);
        participationService = new AuctionParticipationService(auctionService);

        Auction auction = new Auction();
        auction.setId(1L);
        auction.setSellerEmail("seller@test.com");
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(2));

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        Auction scheduledAuction = new Auction();
        scheduledAuction.setId(2L);
        scheduledAuction.setSellerEmail("seller@test.com");
        scheduledAuction.setStatus(AuctionStatus.SCHEDULED);
        when(auctionRepository.findById(2L)).thenReturn(Optional.of(scheduledAuction));
    }

    @Test
    void bidder_CanParticipateInActiveAuction() {
        assertDoesNotThrow(() ->
                participationService.validateParticipation(1L, "bidder@test.com"));
    }

    @Test
    void seller_CannotBidOnOwnAuction() {
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                participationService.validateParticipation(1L, "seller@test.com"));
        assertTrue(ex.getMessage().contains("Sellers cannot place bids"));
    }

    @Test
    void bidder_CannotBidOnScheduledAuction() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                participationService.validateParticipation(2L, "bidder@test.com"));
        assertTrue(ex.getMessage().contains("not accepting bids"));
    }

    @Test
    void preventSellerParticipation_CaseInsensitive() {
        Auction auction = new Auction();
        auction.setSellerEmail("Seller@Test.COM");
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                participationService.preventSellerParticipation(auction, "seller@test.com"));
        assertTrue(ex.getMessage().contains("Sellers cannot place bids"));
    }

    @Test
    void auctionNotFound_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () ->
                participationService.validateParticipation(999L, "bidder@test.com"));
    }
}
