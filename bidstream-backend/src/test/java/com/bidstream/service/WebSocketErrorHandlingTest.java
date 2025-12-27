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
import static org.mockito.Mockito.verify;

class WebSocketErrorHandlingTest {

    private SimpMessagingTemplate messagingTemplate;
    private AuctionEventPublisher auctionEventPublisher;

    @BeforeEach
    void setUp() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        auctionEventPublisher = new AuctionEventPublisher(messagingTemplate);
    }

    @Test
    void testBidRejectedNotification() {
        auctionEventPublisher.publishBidRejectedNotification(1L, "user1@test.com", "Bid too low");
        
        ArgumentCaptor<AuctionEvent> eventCaptor = ArgumentCaptor.forClass(AuctionEvent.class);
        verify(messagingTemplate).convertAndSendToUser(eq("user1@test.com"), eq("/queue/errors"), eventCaptor.capture());
        
        AuctionEvent event = eventCaptor.getValue();
        assertEquals(1L, event.getAuctionId());
        assertEquals(AuctionEvent.EventType.BID_REJECTED, event.getType());
        assertEquals("Bid too low", event.getPayload().get("reason"));
    }
}
