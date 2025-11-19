package com.bidstream.controller;

import com.bidstream.dto.AuctionRequestDto;
import com.bidstream.dto.AuctionResponseDto;
import com.bidstream.entity.Auction;
import com.bidstream.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AuctionResponseDto> createAuction(@Valid @RequestBody AuctionRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sellerEmail = authentication.getName();
        
        Auction auction = new Auction();
        auction.setItemId(requestDto.getItemId());
        auction.setStartTime(requestDto.getStartTime());
        auction.setEndTime(requestDto.getEndTime());
        
        Auction createdAuction = auctionService.createAuction(auction, sellerEmail);
        return ResponseEntity.ok(mapToDto(createdAuction));
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
