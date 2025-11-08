package com.bidstream.repository.mongo;

import com.bidstream.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findBySellerEmail(String sellerEmail);
}
