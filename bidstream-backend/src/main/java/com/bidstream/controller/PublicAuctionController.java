package com.bidstream.controller;

import com.bidstream.dto.AuctionResponseDto;
import com.bidstream.entity.Auction;
import com.bidstream.service.AuctionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/auctions")
public class PublicAuctionController {

    private final AuctionService auctionService;

    public PublicAuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/active")
    public ResponseEntity<Page<AuctionResponseDto>> getActiveAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Auction> activeAuctions = auctionService.getActiveAuctions(pageable);
        return ResponseEntity.ok(activeAuctions.map(this::mapToDto));
    }

    private AuctionResponseDto mapToDto(Auction auction) {
        AuctionResponseDto dto = new AuctionResponseDto();
        dto.setId(auction.getId());
        dto.setItemId(auction.getItemId());
        dto.setSellerEmail(auction.getSellerEmail());
        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());
        dto.setStatus(auction.getStatus());
        dto.setCurrentHighestBid(auction.getCurrentHighestBid());
        dto.setCreatedAt(auction.getCreatedAt());
        return dto;
    }
}
