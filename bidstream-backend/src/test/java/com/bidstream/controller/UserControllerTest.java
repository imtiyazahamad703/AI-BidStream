package com.bidstream.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "SELLER")
    void testSellerDashboardWithSellerRole() throws Exception {
        mockMvc.perform(get("/api/users/seller-dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BIDDER")
    void testSellerDashboardWithBidderRole() throws Exception {
        mockMvc.perform(get("/api/users/seller-dashboard"))
                .andExpect(status().isForbidden());
    }
}
