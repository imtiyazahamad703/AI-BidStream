package com.bidstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bidstream.service.UserService;
import com.bidstream.entity.User;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Optional<User> user = userService.getUserByEmail(currentPrincipalName);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
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
