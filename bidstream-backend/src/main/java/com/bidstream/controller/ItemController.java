package com.bidstream.controller;

import com.bidstream.dto.ItemRequestDto;
import com.bidstream.entity.Item;
import com.bidstream.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sellerEmail = authentication.getName();
        
        Item item = new Item();
        item.setName(requestDto.getName());
        item.setDescription(requestDto.getDescription());
        item.setStartingPrice(requestDto.getStartingPrice());
        item.setAttributes(requestDto.getAttributes());
        
        Item createdItem = itemService.createItem(item, sellerEmail);
        return ResponseEntity.ok(createdItem);
    }

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<Item>> getSellerItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sellerEmail = authentication.getName();
        
        List<Item> items = itemService.getItemsBySeller(sellerEmail);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Item> getItemDetails(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sellerEmail = authentication.getName();
        
        return itemService.getItemById(id)
                .map(item -> {
                    if (!item.getSellerEmail().equals(sellerEmail)) {
                        throw new org.springframework.security.access.AccessDeniedException("You do not own this item");
                    }
                    return ResponseEntity.ok(item);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Item> updateItem(@PathVariable String id, @RequestBody ItemRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sellerEmail = authentication.getName();
        
        Item updatedData = new Item();
        updatedData.setName(requestDto.getName());
        updatedData.setDescription(requestDto.getDescription());
        updatedData.setStartingPrice(requestDto.getStartingPrice());
        updatedData.setAttributes(requestDto.getAttributes());

        Item updatedItem = itemService.updateItem(id, updatedData, sellerEmail);
        return ResponseEntity.ok(updatedItem);
    }
}
