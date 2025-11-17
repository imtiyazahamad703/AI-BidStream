package com.bidstream.service;

import com.bidstream.entity.Item;
import com.bidstream.entity.User;
import com.bidstream.repository.mongo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateItem() {
        Item item = new Item();
        item.setName("Laptop");
        
        User user = new User();
        user.setEmail("seller@test.com");
        
        when(userService.getUserByEmail("seller@test.com")).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        
        Item created = itemService.createItem(item, "seller@test.com");
        
        assertNotNull(created);
        assertEquals("seller@test.com", item.getSellerEmail());
    }

    @Test
    void testDeleteItem_LinkedToAuction() {
        Item item = new Item();
        item.setSellerEmail("seller@test.com");
        item.setAuctionId(1L);
        
        when(itemRepository.findById("1")).thenReturn(Optional.of(item));
        
        assertThrows(IllegalStateException.class, () -> {
            itemService.deleteItem("1", "seller@test.com");
        });
    }

    @Test
    void testVerifyItemOwnership_AccessDenied() {
        Item item = new Item();
        item.setSellerEmail("owner@test.com");

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            itemService.verifyItemOwnership(item, "other@test.com");
        });
    }
}
