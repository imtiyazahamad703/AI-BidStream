package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.entity.Item;
import com.bidstream.repository.jpa.AuctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemService itemService;

    public AuctionService(AuctionRepository auctionRepository, ItemService itemService) {
        this.auctionRepository = auctionRepository;
        this.itemService = itemService;
    }

    @Transactional
    public Auction createAuction(Auction auction, String sellerEmail) {
        // Validate scheduling parameters
        if (auction.getStartTime().isAfter(auction.getEndTime()) || auction.getStartTime().isEqual(auction.getEndTime())) {
            throw new IllegalArgumentException("Auction end time must be after start time");
        }
        
        // Find and verify item
        Item item = itemService.getItemById(auction.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
                
        // Verify ownership
        itemService.verifyItemOwnership(item, sellerEmail);
        
        // Ensure item is not already auctioned
        if (item.getAuctionId() != null) {
            throw new IllegalStateException("Item is already linked to an auction");
        }
        
        if (item.getStatus() != com.bidstream.entity.ItemStatus.AVAILABLE) {
            throw new IllegalStateException("Item is not available for auction");
        }
        
        auction.setSellerEmail(sellerEmail);
        auction.setStatus(AuctionStatus.SCHEDULED);
        auction.setCurrentHighestBid(item.getStartingPrice()); // initialize with starting price
        
        Auction savedAuction = auctionRepository.save(auction);
        
        // Link item to auction
        item.setAuctionId(savedAuction.getId());
        item.setStatus(com.bidstream.entity.ItemStatus.IN_AUCTION);
        itemService.updateItem(item.getId(), item, sellerEmail); // Save changes to Mongo
        
        return savedAuction;
    }

    @Transactional
    public void updateAuctionStatus(Long auctionId, AuctionStatus newStatus) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
                
        // State machine rules
        if (auction.getStatus() == AuctionStatus.COMPLETED || auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a completed or cancelled auction");
        }
        
        if (newStatus == AuctionStatus.COMPLETED || newStatus == AuctionStatus.CANCELLED) {
            Item item = itemService.getItemById(auction.getItemId()).orElse(null);
            if (item != null) {
                item.setStatus(newStatus == AuctionStatus.COMPLETED ? com.bidstream.entity.ItemStatus.SOLD : com.bidstream.entity.ItemStatus.AVAILABLE);
                if (newStatus == AuctionStatus.CANCELLED) {
                    item.setAuctionId(null);
                }
                itemService.updateItem(item.getId(), item, item.getSellerEmail());
            }
        }
        
        auction.setStatus(newStatus);
        auctionRepository.save(auction);
    }
    
    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }
}
