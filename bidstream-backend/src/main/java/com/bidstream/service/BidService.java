package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.Bid;
import com.bidstream.repository.jpa.BidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final AuctionParticipationService participationService;
    private final HighestBidService highestBidService;
    private final RedisBidCacheService redisBidCacheService;

    public BidService(BidRepository bidRepository, 
                      AuctionService auctionService,
                      AuctionParticipationService participationService,
                      HighestBidService highestBidService,
                      RedisBidCacheService redisBidCacheService) {
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
        this.participationService = participationService;
        this.highestBidService = highestBidService;
        this.redisBidCacheService = redisBidCacheService;
    }

    @Transactional
    public Bid placeBid(Long auctionId, String bidderEmail, Double amount) {
        Auction auction = auctionService.getAuctionById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

        // Validate eligibility
        participationService.validateParticipation(auctionId, bidderEmail);

        // Validate minimum increment
        Double currentHighest = highestBidService.getCurrentHighestBid(auctionId).orElse(0.0);
        if (amount <= currentHighest) {
            throw new IllegalArgumentException("Bid amount must be greater than current highest bid: " + currentHighest);
        }

        Bid bid = new Bid();
        bid.setAuctionId(auction.getId());
        bid.setBidderEmail(bidderEmail);
        bid.setAmount(amount);
        bid = bidRepository.save(bid);

        // Update auction
        auction.setCurrentHighestBid(amount);
        auction.setHighestBidderEmail(bidderEmail);
        auctionService.updateAuction(auction); // Needs a method in AuctionService to save

        // Update Redis cache immediately for next concurrent validations
        redisBidCacheService.updateHighestBid(auctionId, amount);

        return bid;
    }

    public org.springframework.data.domain.Page<Bid> getAuctionBids(Long auctionId, org.springframework.data.domain.Pageable pageable) {
        return bidRepository.findByAuctionId(auctionId, pageable);
    }
}
