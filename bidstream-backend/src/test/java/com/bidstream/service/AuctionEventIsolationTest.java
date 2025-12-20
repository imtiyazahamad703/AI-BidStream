package com.bidstream.service;

import com.bidstream.event.AuctionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AuctionEventIsolationTest {

    private SimpMessagingTemplate messagingTemplate;
    private AuctionEventPublisher auctionEventPublisher;

    @BeforeEach
    void setUp() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        auctionEventPublisher = new AuctionEventPublisher(messagingTemplate);
    }

    @Test
    void testEventsIsolatedByAuctionChannel() {
        // Publish bid for auction 1
        auctionEventPublisher.publishBidPlaced(1L, 100.0, "user1@test.com");
        
        // Publish bid for auction 2
        auctionEventPublisher.publishBidPlaced(2L, 200.0, "user2@test.com");
        
        ArgumentCaptor<AuctionEvent> eventCaptor1 = ArgumentCaptor.forClass(AuctionEvent.class);
        ArgumentCaptor<AuctionEvent> eventCaptor2 = ArgumentCaptor.forClass(AuctionEvent.class);
        
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/auction.1"), eventCaptor1.capture());
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/auction.2"), eventCaptor2.capture());
        
        assertEquals(1L, eventCaptor1.getValue().getAuctionId());
        assertEquals(2L, eventCaptor2.getValue().getAuctionId());
    }
}
