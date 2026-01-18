package com.bidstream.repository;

import com.bidstream.domain.DocumentNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentNodeRepository extends MongoRepository<DocumentNode, String> {
    List<DocumentNode> findByAuctionId(Long auctionId);
}
