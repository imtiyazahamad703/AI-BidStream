package com.bidstream.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.bidstream.config.JwtUtil;
import com.bidstream.repository.jpa.AuctionRepository;
import com.bidstream.entity.Auction;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuctionRepository auctionRepository;

    public WebSocketConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService, AuctionRepository auctionRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry the greeting messages back to the client on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic", "/queue", "/topic/auction"); // added /topic/auction explicitly for clarity in AI responses
        // Designates the prefix for messages that are bound for @MessageMapping-annotated methods in controller classes.
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/auction")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor());
    }

    public ChannelInterceptor authInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwt = authHeader.substring(7);
                        String userEmail = jwtUtil.extractUsername(jwt);
                        if (userEmail != null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                            if (jwtUtil.validateToken(jwt, userDetails)) {
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                accessor.setUser(auth);
                            }
                        }
                    }
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/topic/auction.")) {
                        try {
                            Long auctionId = Long.parseLong(destination.substring("/topic/auction.".length()));
                            Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
                            if (auctionOpt.isEmpty() || auctionOpt.get().getStatus() == com.bidstream.entity.AuctionStatus.CANCELLED) {
                                throw new IllegalArgumentException("Unauthorized subscription: Auction not accessible");
                            }
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid auction ID");
                        }
                    }
                }
                return message;
            }
        };
    }
}
