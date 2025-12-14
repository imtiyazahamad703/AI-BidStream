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
        
        BidResponseDto response = mapToDto(bid);
        response.setStatus(com.bidstream.entity.BidStatus.PROCESSING);
        response.setTrackingId(bid.getTrackingId());
        
        return ResponseEntity.accepted().body(response);
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
        Double highestBid = bidService.getHighestBid(auctionId);
        return ResponseEntity.ok(highestBid);
    }

    @GetMapping("/status/{trackingId}")
    public ResponseEntity<java.util.Map<String, String>> getBidStatus(@PathVariable Long auctionId, @PathVariable String trackingId) {
        String status = bidService.getBidStatus(trackingId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(java.util.Map.of("trackingId", trackingId, "status", status));
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
