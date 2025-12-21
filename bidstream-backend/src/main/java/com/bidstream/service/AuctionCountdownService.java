package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.repository.jpa.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class AuctionCountdownService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionCountdownService.class);

    private final AuctionRepository auctionRepository;
    private final AuctionEventPublisher auctionEventPublisher;

    public AuctionCountdownService(AuctionRepository auctionRepository, AuctionEventPublisher auctionEventPublisher) {
        this.auctionRepository = auctionRepository;
        this.auctionEventPublisher = auctionEventPublisher;
    }

    // Run every 10 seconds to broadcast countdowns for active auctions ending soon
    @Scheduled(fixedRate = 10000)
    @Transactional(readOnly = true)
    public void broadcastCountdowns() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusMinutes(5); // Only broadcast countdown for auctions ending in < 5 mins
        
        List<Auction> endingSoonAuctions = auctionRepository.findByStatusAndEndTimeBetween(com.bidstream.entity.AuctionStatus.ACTIVE, now, soon);
        
        for (Auction auction : endingSoonAuctions) {
            long secondsRemaining = ChronoUnit.SECONDS.between(now, auction.getEndTime());
            if (secondsRemaining > 0) {
                auctionEventPublisher.publishEvent(new com.bidstream.event.AuctionEvent(
                    auction.getId(),
                    com.bidstream.event.AuctionEvent.EventType.AUCTION_ENDING,
                    Map.of("secondsRemaining", secondsRemaining)
                ));
            }
        }
    }
}
