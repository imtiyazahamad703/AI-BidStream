package com.bidstream.controller;

import com.bidstream.dto.BidRequestDto;
import com.bidstream.dto.BidResponseDto;
import com.bidstream.entity.Bid;
import com.bidstream.service.BidService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.bidstream.service.HighestBidService;

@RestController
@RequestMapping("/api/auctions/{auctionId}/bids")
public class BidController {

    private final BidService bidService;
    private final HighestBidService highestBidService;

    public BidController(BidService bidService, HighestBidService highestBidService) {
        this.bidService = bidService;
        this.highestBidService = highestBidService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BIDDER')")
    public ResponseEntity<BidResponseDto> placeBid(
            @PathVariable Long auctionId,
            @Valid @RequestBody BidRequestDto requestDto) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String bidderEmail = authentication.getName();
        
        Bid bid = bidService.placeBid(auctionId, bidderEmail, requestDto.getAmount());
        return ResponseEntity.ok(mapToDto(bid));
    }

    @GetMapping
    public ResponseEntity<Page<BidResponseDto>> getBidHistory(
            @PathVariable Long auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Bid> bids = bidService.getAuctionBids(auctionId, pageable);
        return ResponseEntity.ok(bids.map(this::mapToDto));
    }

    @GetMapping("/highest")
    public ResponseEntity<Double> getHighestBid(@PathVariable Long auctionId) {
        Double highest = highestBidService.getCurrentHighestBid(auctionId).orElse(0.0);
        return ResponseEntity.ok(highest);
    }

    private BidResponseDto mapToDto(Bid bid) {
        BidResponseDto dto = new BidResponseDto();
        dto.setId(bid.getId());
        dto.setAuctionId(bid.getAuctionId());
        dto.setBidderEmail(bid.getBidderEmail());
        dto.setAmount(bid.getAmount());
        dto.setCreatedAt(bid.getCreatedAt());
        dto.setStatus(bid.getStatus());
        return dto;
    }
}
