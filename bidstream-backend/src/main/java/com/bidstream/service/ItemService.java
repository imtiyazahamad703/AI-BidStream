package com.bidstream.service;

import com.bidstream.entity.Item;
import com.bidstream.repository.mongo.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(Item item, String sellerEmail) {
        item.setSellerEmail(sellerEmail);
        return itemRepository.save(item);
    }

    public List<Item> getItemsBySeller(String sellerEmail) {
        return itemRepository.findBySellerEmail(sellerEmail);
    }
}
