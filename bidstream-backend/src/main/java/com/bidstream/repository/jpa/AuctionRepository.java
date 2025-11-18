package com.bidstream.repository.jpa;

import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    
    Optional<Auction> findByItemId(String itemId);
    
    Page<Auction> findBySellerEmail(String sellerEmail, Pageable pageable);
    
    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);
    
    List<Auction> findByStatus(AuctionStatus status);
}
