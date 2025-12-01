package com.bidstream.controller;

import com.bidstream.dto.BidRequestDto;
import com.bidstream.dto.BidResponseDto;
import com.bidstream.entity.Bid;
import com.bidstream.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions/{auctionId}/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
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
