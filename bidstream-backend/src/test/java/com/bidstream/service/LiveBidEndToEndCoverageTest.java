package com.bidstream.service;

import com.bidstream.entity.Bid;
import com.bidstream.event.AuctionEvent;
import com.bidstream.event.BidEvent;
import com.bidstream.repository.jpa.BidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LiveBidEndToEndCoverageTest {

    private BidRepository bidRepository;
    private RedisBidCacheService redisBidCacheService;
    private SimpMessagingTemplate messagingTemplate;
    private AuctionEventPublisher auctionEventPublisher;
    private BidEventConsumer bidEventConsumer;

    @BeforeEach
    void setUp() {
        bidRepository = Mockito.mock(BidRepository.class);
        redisBidCacheService = Mockito.mock(RedisBidCacheService.class);
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        auctionEventPublisher = new AuctionEventPublisher(messagingTemplate);
        bidEventConsumer = new BidEventConsumer(bidRepository, redisBidCacheService, auctionEventPublisher);
    }

    @Test
    void testEndToEndBidProcessingToLiveEvent() {
        BidEvent bidEvent = new BidEvent(1L, "bidder1@test.com", 500.0, LocalDateTime.now(), "track-123");
        when(bidRepository.findTopByAuctionIdOrderByAmountDesc(1L)).thenReturn(null);

        bidEventConsumer.consumeBidEvent(bidEvent);

        verify(bidRepository).save(any(Bid.class));
        verify(redisBidCacheService).updateHighestBid(eq(1L), eq(500.0), eq("bidder1@test.com"));
        verify(redisBidCacheService).updateBidStatus("track-123", "ACCEPTED");

        ArgumentCaptor<AuctionEvent> eventCaptor = ArgumentCaptor.forClass(AuctionEvent.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/auction.1"), eventCaptor.capture());

        AuctionEvent publishedEvent = eventCaptor.getValue();
        assertEquals(AuctionEvent.EventType.BID_PLACED, publishedEvent.getType());
        assertEquals(500.0, publishedEvent.getPayload().get("amount"));
        assertEquals("bidder1@test.com", publishedEvent.getPayload().get("bidder"));
        assertEquals(bidEvent.getTimestamp().toString(), publishedEvent.getPayload().get("timestamp"));
    }
}
