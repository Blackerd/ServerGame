package com.example.gameserver.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Cho phép gửi thông điệp tới các topic
        registry.setApplicationDestinationPrefixes("/app"); // Tiền tố cho các message gửi từ client
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/game").setAllowedOrigins("*")
                .withSockJS(); // Đăng ký endpoint "/game" cho WebSocket
    }


}
