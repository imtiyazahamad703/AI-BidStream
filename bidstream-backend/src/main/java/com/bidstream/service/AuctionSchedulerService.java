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
    private final AuctionStateTransitionService transitionService;

    public AuctionSchedulerService(AuctionRepository auctionRepository,
                                   AuctionStateTransitionService transitionService) {
        this.auctionRepository = auctionRepository;
        this.transitionService = transitionService;
    }

    /** Runs every minute — activates auctions whose start time has passed */
    @Scheduled(fixedRate = 60000)
    public void activateScheduledAuctions() {
        logger.info("Checking for scheduled auctions to activate...");
        List<Auction> scheduledAuctions = auctionRepository.findByStatus(AuctionStatus.SCHEDULED);

        LocalDateTime now = LocalDateTime.now();
        for (Auction auction : scheduledAuctions) {
            if (!auction.getStartTime().isAfter(now)) {
                try {
                    transitionService.activate(auction.getId());
                    logger.info("Activated auction ID: {}", auction.getId());
                } catch (Exception e) {
                    logger.error("Failed to activate auction ID: {}", auction.getId(), e);
                }
            }
        }
    }

    /** Runs every minute — completes auctions whose end time has passed */
    @Scheduled(fixedRate = 60000)
    public void completeExpiredAuctions() {
        logger.info("Checking for expired active auctions to complete...");
        List<Auction> activeAuctions = auctionRepository.findByStatus(AuctionStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        for (Auction auction : activeAuctions) {
            if (!auction.getEndTime().isAfter(now)) {
                try {
                    transitionService.complete(auction.getId());
                    logger.info("Completed auction ID: {}", auction.getId());
                } catch (Exception e) {
                    logger.error("Failed to complete auction ID: {}", auction.getId(), e);
                }
            }
        }
    }
}
