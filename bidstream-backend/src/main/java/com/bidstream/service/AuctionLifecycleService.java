package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.repository.jpa.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionLifecycleService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionLifecycleService.class);

    private final AuctionRepository auctionRepository;
    private final AuctionEventPublisher auctionEventPublisher;
    private final BidService bidService;

    public AuctionLifecycleService(AuctionRepository auctionRepository, AuctionEventPublisher auctionEventPublisher, BidService bidService) {
        this.auctionRepository = auctionRepository;
        this.auctionEventPublisher = auctionEventPublisher;
        this.bidService = bidService;
    }

    // Run every 30 seconds to start scheduled auctions
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void startScheduledAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> pendingAuctions = auctionRepository.findByStatusAndStartTimeBefore(com.bidstream.entity.AuctionStatus.SCHEDULED, now);
        
        for (Auction auction : pendingAuctions) {
            auction.setStatus(com.bidstream.entity.AuctionStatus.ACTIVE);
            auctionRepository.save(auction);
            
            logger.info("Started auction {}", auction.getId());
            auctionEventPublisher.publishAuctionStarted(auction.getId());
        }
    }

    // Run every 15 seconds to end completed auctions
    @Scheduled(fixedRate = 15000)
    @Transactional
    public void endCompletedAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> endedAuctions = auctionRepository.findByStatusAndEndTimeBefore(com.bidstream.entity.AuctionStatus.ACTIVE, now);
        
        for (Auction auction : endedAuctions) {
            auction.setStatus(com.bidstream.entity.AuctionStatus.COMPLETED);
            auctionRepository.save(auction);
            
            // Get highest bid info to broadcast winner
            Double finalAmount = bidService.getHighestBid(auction.getId());
            
            logger.info("Ended auction {}", auction.getId());
            auctionEventPublisher.publishAuctionEnded(auction.getId(), "winner@placeholder.com", finalAmount);
        }
    }
}
