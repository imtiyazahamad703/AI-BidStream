package com.bidstream.repository.mongo;

import com.bidstream.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    Page<Item> findBySellerEmail(String sellerEmail, Pageable pageable);
    List<Item> findBySellerEmail(String sellerEmail);
}
