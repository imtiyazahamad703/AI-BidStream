package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.event.AuctionEvent;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuctionCountdownEventTest {

    private AuctionRepository auctionRepository;
    private AuctionEventPublisher auctionEventPublisher;
    private AuctionCountdownService auctionCountdownService;

    @BeforeEach
    void setUp() {
        auctionRepository = Mockito.mock(AuctionRepository.class);
        auctionEventPublisher = Mockito.mock(AuctionEventPublisher.class);
        auctionCountdownService = new AuctionCountdownService(auctionRepository, auctionEventPublisher);
    }

    @Test
    void testBroadcastCountdowns() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setEndTime(LocalDateTime.now().plusSeconds(60));
        
        when(auctionRepository.findByStatusAndEndTimeBetween(
                eq(com.bidstream.entity.AuctionStatus.ACTIVE), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(auction));
                
        auctionCountdownService.broadcastCountdowns();
        
        ArgumentCaptor<AuctionEvent> eventCaptor = ArgumentCaptor.forClass(AuctionEvent.class);
        verify(auctionEventPublisher).publishEvent(eventCaptor.capture());
        
        AuctionEvent event = eventCaptor.getValue();
        assertEquals(1L, event.getAuctionId());
        assertEquals(AuctionEvent.EventType.AUCTION_ENDING, event.getType());
        assertNotNull(event.getPayload().get("secondsRemaining"));
    }
}
