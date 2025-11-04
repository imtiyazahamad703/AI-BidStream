package com.bidstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return ResponseEntity.ok("User Profile for: " + currentPrincipalName);
    }

    @GetMapping("/seller-dashboard")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> getSellerDashboard() {
        return ResponseEntity.ok("Seller dashboard data");
    }

    @GetMapping("/bidder-dashboard")
    @PreAuthorize("hasRole('BIDDER')")
    public ResponseEntity<String> getBidderDashboard() {
        return ResponseEntity.ok("Bidder dashboard data");
    }
}
