package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuctionStateTransitionServiceTest {

    private AuctionRepository auctionRepository;
    private AuctionStateTransitionService transitionService;

    @BeforeEach
    void setUp() {
        auctionRepository = Mockito.mock(AuctionRepository.class);
        ItemService itemService = Mockito.mock(ItemService.class);
        AuctionService auctionService = new AuctionService(auctionRepository, itemService);
        transitionService = new AuctionStateTransitionService(auctionRepository, auctionService);
    }

    private Auction auctionWithStatus(Long id, AuctionStatus status) {
        Auction a = new Auction();
        a.setId(id);
        a.setSellerEmail("seller@test.com");
        a.setStatus(status);
        return a;
    }

    // ── activate ────────────────────────────────────────────────────────────

    @Test
    void activate_FromScheduled_ShouldSucceed() {
        Auction auction = auctionWithStatus(1L, AuctionStatus.SCHEDULED);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(auction)).thenReturn(auction);

        assertDoesNotThrow(() -> transitionService.activate(1L));
        assertEquals(AuctionStatus.ACTIVE, auction.getStatus());
    }

    @Test
    void activate_FromActive_ShouldThrow() {
        Auction auction = auctionWithStatus(2L, AuctionStatus.ACTIVE);
        when(auctionRepository.findById(2L)).thenReturn(Optional.of(auction));

        assertThrows(IllegalStateException.class, () -> transitionService.activate(2L));
    }

    @Test
    void activate_FromCompleted_ShouldThrow() {
        Auction auction = auctionWithStatus(3L, AuctionStatus.COMPLETED);
        when(auctionRepository.findById(3L)).thenReturn(Optional.of(auction));

        assertThrows(IllegalStateException.class, () -> transitionService.activate(3L));
    }

    // ── complete ────────────────────────────────────────────────────────────

    @Test
    void complete_FromActive_ShouldSucceed() {
        Auction auction = auctionWithStatus(4L, AuctionStatus.ACTIVE);
        when(auctionRepository.findById(4L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(auction)).thenReturn(auction);

        assertDoesNotThrow(() -> transitionService.complete(4L));
        assertEquals(AuctionStatus.COMPLETED, auction.getStatus());
    }

    @Test
    void complete_FromScheduled_ShouldThrow() {
        Auction auction = auctionWithStatus(5L, AuctionStatus.SCHEDULED);
        when(auctionRepository.findById(5L)).thenReturn(Optional.of(auction));

        assertThrows(IllegalStateException.class, () -> transitionService.complete(5L));
    }

    // ── cancel ──────────────────────────────────────────────────────────────

    @Test
    void cancel_FromScheduled_ShouldSucceed() {
        Auction auction = auctionWithStatus(6L, AuctionStatus.SCHEDULED);
        when(auctionRepository.findById(6L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(auction)).thenReturn(auction);

        assertDoesNotThrow(() -> transitionService.cancel(6L));
        assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
    }

    @Test
    void cancel_FromCompleted_ShouldThrow() {
        Auction auction = auctionWithStatus(7L, AuctionStatus.COMPLETED);
        when(auctionRepository.findById(7L)).thenReturn(Optional.of(auction));

        assertThrows(IllegalStateException.class, () -> transitionService.cancel(7L));
    }

    @Test
    void cancel_AlreadyCancelled_ShouldThrow() {
        Auction auction = auctionWithStatus(8L, AuctionStatus.CANCELLED);
        when(auctionRepository.findById(8L)).thenReturn(Optional.of(auction));

        assertThrows(IllegalStateException.class, () -> transitionService.cancel(8L));
    }

    @Test
    void transition_AuctionNotFound_ShouldThrow() {
        when(auctionRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> transitionService.activate(999L));
        assertThrows(IllegalArgumentException.class, () -> transitionService.complete(999L));
        assertThrows(IllegalArgumentException.class, () -> transitionService.cancel(999L));
    }
}
