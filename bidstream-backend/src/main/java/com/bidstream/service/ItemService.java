package com.bidstream.service;

import com.bidstream.entity.Item;
import com.bidstream.repository.mongo.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public Item createItem(Item item, String sellerEmail) {
        userService.getUserByEmail(sellerEmail)
            .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
            
        item.setSellerEmail(sellerEmail);
        return itemRepository.save(item);
    }

    public List<Item> getItemsBySeller(String sellerEmail) {
        return itemRepository.findBySellerEmail(sellerEmail);
    }

    public Optional<Item> getItemById(String id) {
        return itemRepository.findById(id);
    }
}
