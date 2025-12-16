package com.bidstream.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to route messages back to the client
        // on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic");
        
        // Prefix for messages that are bound for @MessageMapping-annotated methods
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the STOMP endpoint the clients will connect to
        // withSockJS is often added for fallback options, but we can stick to native WebSockets or allow CORS
        registry.addEndpoint("/ws/auction")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
