package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.entity.Item;
import com.bidstream.entity.ItemStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuctionSchedulingTest {

    private AuctionRepository auctionRepository;
    private ItemService itemService;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionRepository = Mockito.mock(AuctionRepository.class);
        itemService = Mockito.mock(ItemService.class);
        auctionService = new AuctionService(auctionRepository, itemService);
    }

    // ── Start-time validation ───────────────────────────────────────────────

    @Test
    void startTime_InThePast_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.validateAuctionStartTime(LocalDateTime.now().minusHours(1)));
        assertTrue(ex.getMessage().contains("5 minutes in the future"));
    }

    @Test
    void startTime_LessThan5MinutesAhead_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.validateAuctionStartTime(LocalDateTime.now().plusMinutes(3)));
        assertTrue(ex.getMessage().contains("5 minutes in the future"));
    }

    @Test
    void startTime_ValidFutureTime_ShouldPass() {
        assertDoesNotThrow(() ->
                auctionService.validateAuctionStartTime(LocalDateTime.now().plusHours(1)));
    }

    // ── End-time validation ─────────────────────────────────────────────────

    @Test
    void endTime_BeforeStartTime_ShouldThrow() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.minusMinutes(30);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.validateAuctionEndTime(start, end));
        assertTrue(ex.getMessage().contains("after start time"));
    }

    @Test
    void endTime_LessThan1HourAfterStart_ShouldThrow() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusMinutes(30); // only 30 min duration
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.validateAuctionEndTime(start, end));
        assertTrue(ex.getMessage().contains("at least 1 hour"));
    }

    @Test
    void endTime_MoreThan14DaysAfterStart_ShouldThrow() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusDays(15);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                auctionService.validateAuctionEndTime(start, end));
        assertTrue(ex.getMessage().contains("14 days"));
    }

    @Test
    void endTime_ValidRange_ShouldPass() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusDays(3);
        assertDoesNotThrow(() -> auctionService.validateAuctionEndTime(start, end));
    }

    // ── Full createAuction scheduling path ──────────────────────────────────

    @Test
    void createAuction_InvalidStartTime_ShouldRejectBeforeDbQuery() {
        Auction auction = new Auction();
        auction.setItemId("item1");
        auction.setStartTime(LocalDateTime.now().minusHours(1)); // past
        auction.setEndTime(LocalDateTime.now().plusDays(2));

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.createAuction(auction, "seller@test.com"));
        // Verify itemService was never called — fail-fast behaviour
        Mockito.verifyNoInteractions(itemService);
    }
}
