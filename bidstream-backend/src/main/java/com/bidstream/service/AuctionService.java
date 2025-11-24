package com.bidstream.service;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.entity.Item;
import com.bidstream.entity.ItemStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
        validateAuctionStartTime(auction.getStartTime());
        validateAuctionEndTime(auction.getStartTime(), auction.getEndTime());
        
        // Find and verify item
        Item item = itemService.getItemById(auction.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
                
        // Verify ownership
        itemService.verifyItemOwnership(item, sellerEmail);
        
        // Ensure item is not already auctioned
        if (item.getAuctionId() != null) {
            throw new IllegalStateException("Item is already linked to an auction");
        }
        
        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalStateException("Item is not available for auction");
        }
        
        auction.setSellerEmail(sellerEmail);
        auction.setStatus(AuctionStatus.SCHEDULED);
        auction.setCurrentHighestBid(item.getStartingPrice()); // initialize with starting price
        
        Auction savedAuction = auctionRepository.save(auction);
        
        // Link item to auction
        item.setAuctionId(savedAuction.getId());
        item.setStatus(ItemStatus.IN_AUCTION);
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
                item.setStatus(newStatus == AuctionStatus.COMPLETED ? ItemStatus.SOLD : ItemStatus.AVAILABLE);
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
    
    public Page<Auction> getActiveAuctions(Pageable pageable) {
        return auctionRepository.findByStatus(AuctionStatus.ACTIVE, pageable);
    }
    
    public Page<Auction> getAuctionsBySeller(String sellerEmail, Pageable pageable) {
        return auctionRepository.findBySellerEmail(sellerEmail, pageable);
    }
    
    public void verifyAuctionOwnership(Auction auction, String sellerEmail) {
        if (!auction.getSellerEmail().equals(sellerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not own this auction");
        }
    }

    /**
     * Public auction search with optional status filter.
     * If status is null, returns all auctions (paginated).
     */
    public Page<Auction> searchAuctions(AuctionStatus status, Pageable pageable) {
        if (status != null) {
            return auctionRepository.findByStatus(status, pageable);
        }
        return auctionRepository.findAll(pageable);
    }

    /**
     * Validates auction start time:
     * - Must not be null
     * - Must be at least 5 minutes in the future
     */
    public void validateAuctionStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Auction start time is required");
        }
        if (!startTime.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new IllegalArgumentException("Auction must start at least 5 minutes in the future");
        }
    }

    /**
     * Validates auction end time:
     * - Must not be null
     * - Must be strictly after start time
     * - Minimum duration: 1 hour
     */
    public void validateAuctionEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("Auction end time is required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Auction end time must be after start time");
        }
        long minutesBetween = ChronoUnit.MINUTES.between(startTime, endTime);
        if (minutesBetween < 60) {
            throw new IllegalArgumentException("Auction must run for at least 1 hour");
        }
        long daysBetween = ChronoUnit.DAYS.between(startTime, endTime);
        if (daysBetween > 14) {
            throw new IllegalArgumentException("Auction duration cannot exceed 14 days");
        }
    }
}
