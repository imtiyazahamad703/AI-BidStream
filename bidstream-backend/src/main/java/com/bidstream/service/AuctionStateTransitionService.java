package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages explicit, validated state transitions for auctions.
 * Acts as the single point of truth for status changes,
 * ensuring the state machine rules are always enforced.
 *
 * Valid transitions:
 *   SCHEDULED  → ACTIVE    (by scheduler or admin)
 *   SCHEDULED  → CANCELLED (by seller or admin)
 *   ACTIVE     → COMPLETED (by scheduler when endTime passes)
 *   ACTIVE     → CANCELLED (by admin in exceptional cases)
 */
@Service
public class AuctionStateTransitionService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionStateTransitionService.class);

    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    public AuctionStateTransitionService(AuctionRepository auctionRepository,
                                         AuctionService auctionService) {
        this.auctionRepository = auctionRepository;
        this.auctionService = auctionService;
    }

    /**
     * Transition SCHEDULED → ACTIVE when start time has passed.
     */
    @Transactional
    public void activate(Long auctionId) {
        Auction auction = fetchAuction(auctionId);

        if (auction.getStatus() != AuctionStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Cannot activate auction in state: " + auction.getStatus());
        }

        auction.setStatus(AuctionStatus.ACTIVE);
        auctionRepository.save(auction);
        logger.info("Auction {} transitioned SCHEDULED → ACTIVE", auctionId);
    }

    /**
     * Transition ACTIVE → COMPLETED when end time has passed.
     */
    @Transactional
    public void complete(Long auctionId) {
        Auction auction = fetchAuction(auctionId);

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Cannot complete auction in state: " + auction.getStatus());
        }

        auction.setStatus(AuctionStatus.COMPLETED);
        auctionRepository.save(auction);
        logger.info("Auction {} transitioned ACTIVE → COMPLETED", auctionId);
    }

    /**
     * Transition SCHEDULED or ACTIVE → CANCELLED.
     */
    @Transactional
    public void cancel(Long auctionId) {
        Auction auction = fetchAuction(auctionId);

        if (auction.getStatus() == AuctionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed auction");
        }
        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException("Auction is already cancelled");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.save(auction);
        logger.info("Auction {} transitioned → CANCELLED", auctionId);
    }

    private Auction fetchAuction(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));
    }
}
