package com.bidstream.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentWebSocketSubscribersTest {

    private ParticipantTrackerService participantTrackerService;

    @BeforeEach
    void setUp() {
        participantTrackerService = new ParticipantTrackerService();
    }

    @Test
    void testConcurrentSubscribers() throws InterruptedException {
        int numberOfSubscribers = 500;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfSubscribers);
        AtomicInteger failures = new AtomicInteger();

        for (int i = 0; i < numberOfSubscribers; i++) {
            final String sessionId = "session-" + i;
            executor.submit(() -> {
                try {
                    participantTrackerService.addParticipant(1L, sessionId);
                } catch (Exception e) {
                    failures.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(0, failures.get());
        assertEquals(numberOfSubscribers, participantTrackerService.getActiveBidderCount(1L));
        
        executor.shutdown();
    }
}
