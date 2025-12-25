package com.bidstream.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParticipantTrackerService {

    // Maps auctionId to a set of session IDs
    private final ConcurrentHashMap<Long, Set<String>> auctionParticipants = new ConcurrentHashMap<>();
    
    // Maps sessionId to auctionId
    private final ConcurrentHashMap<String, Long> sessionAuctionMap = new ConcurrentHashMap<>();

    public void addParticipant(Long auctionId, String sessionId) {
        auctionParticipants.computeIfAbsent(auctionId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionAuctionMap.put(sessionId, auctionId);
    }

    public void removeParticipant(String sessionId) {
        Long auctionId = sessionAuctionMap.remove(sessionId);
        if (auctionId != null) {
            Set<String> sessions = auctionParticipants.get(auctionId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    auctionParticipants.remove(auctionId);
                }
            }
        }
    }

    public int getActiveBidderCount(Long auctionId) {
        Set<String> sessions = auctionParticipants.get(auctionId);
        return sessions != null ? sessions.size() : 0;
    }
}
