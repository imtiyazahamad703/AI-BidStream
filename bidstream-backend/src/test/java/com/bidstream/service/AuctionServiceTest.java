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

class AuctionServiceTest {

    private AuctionRepository auctionRepository;
    private ItemService itemService;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionRepository = Mockito.mock(AuctionRepository.class);
        itemService = Mockito.mock(ItemService.class);
        auctionService = new AuctionService(auctionRepository, itemService);
    }

    @Test
    void testCreateAuction_Success() {
        Auction auction = new Auction();
        auction.setItemId("item1");
        auction.setStartTime(LocalDateTime.now().plusDays(1));
        auction.setEndTime(LocalDateTime.now().plusDays(2));

        Item item = new Item();
        item.setId("item1");
        item.setSellerEmail("seller@test.com");
        item.setStatus(ItemStatus.AVAILABLE);
        item.setStartingPrice(100.0);

        when(itemService.getItemById("item1")).thenReturn(Optional.of(item));
        when(auctionRepository.save(any(Auction.class))).thenAnswer(invocation -> {
            Auction saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Auction created = auctionService.createAuction(auction, "seller@test.com");

        assertNotNull(created.getId());
        assertEquals(AuctionStatus.SCHEDULED, created.getStatus());
        assertEquals(100.0, created.getCurrentHighestBid());
        assertEquals(ItemStatus.IN_AUCTION, item.getStatus());
        assertEquals(1L, item.getAuctionId());
    }

    @Test
    void testCreateAuction_InvalidSchedule() {
        Auction auction = new Auction();
        auction.setItemId("item1");
        auction.setStartTime(LocalDateTime.now().plusDays(2));
        auction.setEndTime(LocalDateTime.now().plusDays(1)); // End time before start time

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            auctionService.createAuction(auction, "seller@test.com");
        });

        assertEquals("Auction end time must be after start time", exception.getMessage());
    }

    @Test
    void testCreateAuction_ItemNotAvailable() {
        Auction auction = new Auction();
        auction.setItemId("item1");
        auction.setStartTime(LocalDateTime.now().plusDays(1));
        auction.setEndTime(LocalDateTime.now().plusDays(2));

        Item item = new Item();
        item.setId("item1");
        item.setSellerEmail("seller@test.com");
        item.setStatus(ItemStatus.SOLD); // Not AVAILABLE

        when(itemService.getItemById("item1")).thenReturn(Optional.of(item));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            auctionService.createAuction(auction, "seller@test.com");
        });

        assertEquals("Item is not available for auction", exception.getMessage());
    }
}
