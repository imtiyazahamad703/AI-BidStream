package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionSchedulerService.class);
    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    public AuctionSchedulerService(AuctionRepository auctionRepository, AuctionService auctionService) {
        this.auctionRepository = auctionRepository;
        this.auctionService = auctionService;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void activateScheduledAuctions() {
        logger.info("Checking for scheduled auctions to activate...");
        List<Auction> scheduledAuctions = auctionRepository.findByStatus(AuctionStatus.SCHEDULED);
        
        LocalDateTime now = LocalDateTime.now();
        for (Auction auction : scheduledAuctions) {
            if (auction.getStartTime().isBefore(now) || auction.getStartTime().isEqual(now)) {
                try {
                    auctionService.updateAuctionStatus(auction.getId(), AuctionStatus.ACTIVE);
                    logger.info("Activated auction ID: {}", auction.getId());
                } catch (Exception e) {
                    logger.error("Failed to activate auction ID: {}", auction.getId(), e);
                }
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void completeExpiredAuctions() {
        logger.info("Checking for expired active auctions to complete...");
        List<Auction> activeAuctions = auctionRepository.findByStatus(AuctionStatus.ACTIVE);
        
        LocalDateTime now = LocalDateTime.now();
        for (Auction auction : activeAuctions) {
            if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                try {
                    auctionService.updateAuctionStatus(auction.getId(), AuctionStatus.COMPLETED);
                    logger.info("Completed auction ID: {}", auction.getId());
                } catch (Exception e) {
                    logger.error("Failed to complete auction ID: {}", auction.getId(), e);
                }
            }
        }
    }
}
