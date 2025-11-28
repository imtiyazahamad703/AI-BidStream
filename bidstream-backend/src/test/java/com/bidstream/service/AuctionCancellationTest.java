package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.entity.Item;
import com.bidstream.entity.ItemStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AuctionCancellationTest {

    private AuctionRepository auctionRepository;
    private ItemService itemService;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionRepository = Mockito.mock(AuctionRepository.class);
        itemService = Mockito.mock(ItemService.class);
        auctionService = new AuctionService(auctionRepository, itemService);
    }

    private Auction scheduledAuction() {
        Auction a = new Auction();
        a.setId(1L);
        a.setItemId("item-1");
        a.setSellerEmail("seller@test.com");
        a.setStatus(AuctionStatus.SCHEDULED);
        return a;
    }

    @Test
    void cancel_ScheduledAuction_ShouldSucceed() {
        Auction auction = scheduledAuction();
        Item item = new Item();
        item.setId("item-1");
        item.setSellerEmail("seller@test.com");
        item.setStatus(ItemStatus.IN_AUCTION);
        item.setAuctionId(1L);

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));
        when(auctionRepository.save(any())).thenReturn(auction);
        when(itemService.getItemById("item-1")).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> auctionService.cancelAuction(1L, "seller@test.com"));

        assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
        assertEquals(ItemStatus.AVAILABLE, item.getStatus());
        assertNull(item.getAuctionId());
    }

    @Test
    void cancel_ActiveAuction_ShouldThrow() {
        Auction auction = scheduledAuction();
        auction.setStatus(AuctionStatus.ACTIVE);

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                auctionService.cancelAuction(1L, "seller@test.com"));
        assertTrue(ex.getMessage().contains("SCHEDULED"));
    }

    @Test
    void cancel_CompletedAuction_ShouldThrow() {
        Auction auction = scheduledAuction();
        auction.setStatus(AuctionStatus.COMPLETED);

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        assertThrows(IllegalStateException.class, () ->
                auctionService.cancelAuction(1L, "seller@test.com"));
    }

    @Test
    void cancel_ByNonOwner_ShouldThrow() {
        Auction auction = scheduledAuction();
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        assertThrows(AccessDeniedException.class, () ->
                auctionService.cancelAuction(1L, "other@test.com"));
    }

    @Test
    void cancel_NotFound_ShouldThrow() {
        when(auctionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.cancelAuction(999L, "seller@test.com"));
    }
}
