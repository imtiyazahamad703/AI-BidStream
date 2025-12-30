package com.bidstream.service;

import com.bidstream.event.AuctionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

class LiveAuctionEventThroughputTest {

    private SimpMessagingTemplate messagingTemplate;
    private AuctionEventPublisher auctionEventPublisher;

    @BeforeEach
    void setUp() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        auctionEventPublisher = new AuctionEventPublisher(messagingTemplate);
    }

    @Test
    void testConcurrentEventBroadcasting() throws InterruptedException {
        int numberOfEvents = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfEvents);

        for (int i = 0; i < numberOfEvents; i++) {
            final double amount = 100.0 + i;
            executor.submit(() -> {
                try {
                    auctionEventPublisher.publishBidPlaced(1L, amount, "user@test.com", null);
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        verify(messagingTemplate, atLeast(numberOfEvents)).convertAndSend(anyString(), any(AuctionEvent.class));
        
        executor.shutdown();
    }
}
