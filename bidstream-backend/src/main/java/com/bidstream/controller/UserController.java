package com.bidstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/seller-dashboard")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> getSellerDashboard() {
        return ResponseEntity.ok("Seller dashboard data");
    }
}
