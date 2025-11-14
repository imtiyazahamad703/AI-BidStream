package com.bidstream.controller;

import com.bidstream.dto.ItemRequestDto;
import com.bidstream.entity.Item;
import com.bidstream.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "seller@test.com", roles = {"SELLER"})
    void testCreateItem() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setName("Antique Clock");
        request.setDescription("A beautiful antique clock.");
        request.setStartingPrice(150.0);

        Item item = new Item();
        item.setId("item123");
        item.setName("Antique Clock");
        item.setStartingPrice(150.0);
        item.setSellerEmail("seller@test.com");

        Mockito.when(itemService.createItem(any(Item.class), eq("seller@test.com"))).thenReturn(item);

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("item123"))
                .andExpect(jsonPath("$.name").value("Antique Clock"));
    }
}
