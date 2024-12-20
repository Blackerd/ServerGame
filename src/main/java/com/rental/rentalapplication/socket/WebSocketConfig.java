package com.rental.rentalapplication.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AuthenticationWebSocketHandler authenticationWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(AuthenticationWebSocketHandler authenticationWebSocketHandler,
                           JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.authenticationWebSocketHandler = authenticationWebSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(authenticationWebSocketHandler, "/game")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
