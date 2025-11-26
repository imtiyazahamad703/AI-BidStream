package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import org.springframework.stereotype.Service;

/**
 * Validates whether a user is permitted to participate (bid) in an auction.
 * Business rules:
 * - Sellers cannot bid on auctions they created
 * - Only ACTIVE auctions accept bids
 * - Bidders must be authenticated users (enforced at controller level)
 */
@Service
public class AuctionParticipationService {

    private final AuctionService auctionService;

    public AuctionParticipationService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Validates that the given bidder is allowed to place a bid on the given auction.
     *
     * @param auctionId   The auction to bid on
     * @param bidderEmail The email of the user attempting to bid
     */
    public void validateParticipation(Long auctionId, String bidderEmail) {
        Auction auction = auctionService.getAuctionById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

        // Rule 1: Auction must be ACTIVE
        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Auction is not accepting bids. Current status: " + auction.getStatus());
        }

        // Rule 2: Seller cannot bid on their own auction
        preventSellerParticipation(auction, bidderEmail);
    }

    /**
     * Prevents the auction's seller from bidding on their own auction.
     */
    public void preventSellerParticipation(Auction auction, String bidderEmail) {
        if (auction.getSellerEmail().equalsIgnoreCase(bidderEmail)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Sellers cannot place bids on their own auctions");
        }
    }
}
