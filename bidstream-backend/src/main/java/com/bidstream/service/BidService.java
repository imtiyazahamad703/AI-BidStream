package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.Bid;
import com.bidstream.repository.jpa.BidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bidstream.event.BidEvent;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final AuctionParticipationService participationService;
    private final HighestBidService highestBidService;
    private final RedisBidCacheService redisBidCacheService;
    private final AuctionLockService lockService;
    private final BidEventProducer bidEventProducer;

    public BidService(BidRepository bidRepository, 
                      AuctionService auctionService,
                      AuctionParticipationService participationService,
                      HighestBidService highestBidService,
                      RedisBidCacheService redisBidCacheService,
                      AuctionLockService lockService,
                      BidEventProducer bidEventProducer) {
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
        this.participationService = participationService;
        this.highestBidService = highestBidService;
        this.redisBidCacheService = redisBidCacheService;
        this.lockService = lockService;
        this.bidEventProducer = bidEventProducer;
    }

    @Transactional
    public Bid placeBid(Long auctionId, String bidderEmail, Double amount) {
        // Attempt to acquire lock for concurrent safety
        boolean locked = lockService.tryLock(auctionId);
        if (!locked) {
            throw new IllegalStateException("High bid volume. Please try again in a moment.");
        }

        try {
            Auction auction = auctionService.getAuctionById(auctionId)
                    .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

            // Validate eligibility
            participationService.validateParticipation(auctionId, bidderEmail);

            // Validate minimum increment against current cached highest bid
            Double currentHighest = highestBidService.getCurrentHighestBid(auctionId).orElse(0.0);
            if (amount <= currentHighest) {
                throw new IllegalArgumentException("Bid amount must be greater than current highest bid: " + currentHighest);
            }

            Bid bid = new Bid();
            bid.setAuctionId(auction.getId());
            bid.setBidderEmail(bidderEmail);
            bid.setAmount(amount);

            // Update Redis cache immediately for next concurrent validations
            redisBidCacheService.updateHighestBid(auctionId, amount);

            String trackingId = java.util.UUID.randomUUID().toString();
            bid.setTrackingId(trackingId);
            redisBidCacheService.updateBidStatus(trackingId, "PROCESSING");

            // Publish event to Kafka
            BidEvent event = new BidEvent(auctionId, bidderEmail, amount, java.time.LocalDateTime.now(), trackingId);
            bidEventProducer.publishBidEvent(event);

            return bid;
        } finally {
            lockService.unlock(auctionId);
        }
    }

    public Double getHighestBid(Long auctionId) {
        return highestBidService.getCurrentHighestBid(auctionId).orElse(0.0);
    }

    public String getBidStatus(String trackingId) {
        return redisBidCacheService.getBidStatus(trackingId);
    }

    public org.springframework.data.domain.Page<Bid> getAuctionBids(Long auctionId, org.springframework.data.domain.Pageable pageable) {
        return bidRepository.findByAuctionId(auctionId, pageable);
    }
}
