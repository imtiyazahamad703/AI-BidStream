package com.bidstream.service;

import com.bidstream.entity.Bid;
import com.bidstream.repository.jpa.BidRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HighestBidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;

    public HighestBidService(BidRepository bidRepository, AuctionService auctionService) {
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
    }

    /**
     * Gets the current highest bid for an auction from the database.
     */
    public Optional<Double> getCurrentHighestBid(Long auctionId) {
        Bid topBid = bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId);
        if (topBid != null) {
            return Optional.of(topBid.getAmount());
        }
        
        // If no bids yet, return the starting price (which is stored in currentHighestBid in Auction)
        return auctionService.getAuctionById(auctionId)
                .map(auction -> auction.getCurrentHighestBid());
    }
}
