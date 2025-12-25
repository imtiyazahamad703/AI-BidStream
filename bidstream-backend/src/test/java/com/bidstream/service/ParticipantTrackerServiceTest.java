package com.bidstream.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipantTrackerServiceTest {

    private ParticipantTrackerService trackerService;

    @BeforeEach
    void setUp() {
        trackerService = new ParticipantTrackerService();
    }

    @Test
    void testParticipantTracking() {
        assertEquals(0, trackerService.getActiveBidderCount(1L));
        
        trackerService.addParticipant(1L, "session-1");
        assertEquals(1, trackerService.getActiveBidderCount(1L));
        
        trackerService.addParticipant(1L, "session-2");
        assertEquals(2, trackerService.getActiveBidderCount(1L));
        
        trackerService.removeParticipant("session-1");
        assertEquals(1, trackerService.getActiveBidderCount(1L));
        
        trackerService.removeParticipant("session-2");
        assertEquals(0, trackerService.getActiveBidderCount(1L));
    }
}
