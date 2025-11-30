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

    public BidService(BidRepository bidRepository, AuctionService auctionService) {
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
    }

    @Transactional
    public Bid placeBid(Long auctionId, String bidderEmail, Double amount) {
        Auction auction = auctionService.getAuctionById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

        Bid bid = new Bid();
        bid.setAuctionId(auction.getId());
        bid.setBidderEmail(bidderEmail);
        bid.setAmount(amount);

        return bidRepository.save(bid);
    }
}
