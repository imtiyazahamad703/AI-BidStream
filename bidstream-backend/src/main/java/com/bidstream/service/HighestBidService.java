package com.bidstream.service;

import com.bidstream.entity.Bid;
import com.bidstream.repository.jpa.BidRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HighestBidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final RedisBidCacheService redisBidCacheService;

    public HighestBidService(BidRepository bidRepository, 
                             AuctionService auctionService,
                             RedisBidCacheService redisBidCacheService) {
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
        this.redisBidCacheService = redisBidCacheService;
    }

    /**
     * Gets the current highest bid for an auction from the database.
     */
    public Optional<Double> getCurrentHighestBid(Long auctionId) {
        // First try Redis Cache
        Optional<Double> cachedBid = redisBidCacheService.getHighestBid(auctionId);
        if (cachedBid.isPresent()) {
            return cachedBid;
        }

        // Fallback to database
        Bid topBid = bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId);
        if (topBid != null) {
            redisBidCacheService.updateHighestBid(auctionId, topBid.getAmount(), topBid.getBidderEmail());
            return Optional.of(topBid.getAmount());
        }
        
        // If no bids yet, return the starting price (which is stored in currentHighestBid in Auction)
        return auctionService.getAuctionById(auctionId)
                .map(auction -> {
                    Double startingPrice = auction.getCurrentHighestBid();
                    redisBidCacheService.initializeAuctionState(auctionId, startingPrice);
                    return startingPrice;
                });
    }
}
