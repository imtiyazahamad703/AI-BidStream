package com.bidstream.service;

import com.bidstream.config.JwtUtil;
import com.bidstream.config.WebSocketConfig;
import com.bidstream.entity.Auction;
import com.bidstream.entity.AuctionStatus;
import com.bidstream.repository.jpa.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class WebSocketSubscriptionAuthTest {

    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;
    private AuctionRepository auctionRepository;
    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        jwtUtil = Mockito.mock(JwtUtil.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        auctionRepository = Mockito.mock(AuctionRepository.class);
        webSocketConfig = new WebSocketConfig(jwtUtil, userDetailsService, auctionRepository);
    }

    @Test
    void testSubscribeToAccessibleAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.ACTIVE);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        ChannelInterceptor interceptor = webSocketConfig.authInterceptor();
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/auction.1");
        
        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        
        Message<?> processedMessage = interceptor.preSend(message, null);
        assertNotNull(processedMessage);
    }

    @Test
    void testSubscribeToCancelledAuctionThrowsException() {
        Auction auction = new Auction();
        auction.setId(2L);
        auction.setStatus(AuctionStatus.CANCELLED);
        when(auctionRepository.findById(2L)).thenReturn(Optional.of(auction));

        ChannelInterceptor interceptor = webSocketConfig.authInterceptor();
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/auction.2");
        
        Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        
        assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(message, null));
    }
}
