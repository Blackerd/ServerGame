package com.example.gameserver.socket;


import com.example.gameserver.model.GameSession;
import com.example.gameserver.model.GameSessionRepository;
import com.example.gameserver.model.Player;
import com.example.gameserver.server.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final AuthService authService;
    private final GameSessionRepository gameSessionRepository;
    @Autowired
    public GameWebSocketHandler(AuthService authService, GameSessionRepository gameSessionRepository) {
        this.authService = authService;
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Player connected: " + session.getId());
        // Lưu kết nối WebSocket của người chơi trong session hoặc game session
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();

        try {
            if (messageContent.startsWith("SAVE_SESSION")) {
                String[] parts = messageContent.split("\\|");
                if (parts.length < 4) {
                    session.sendMessage(new TextMessage("Error: Invalid message format."));
                    return;
                }

                int score = Integer.parseInt(parts[1]);
                long duration = Long.parseLong(parts[2]);
                String username = parts[3];

                Player player = authService.getPlayerByUsername(username);
                if (player == null) {
                    session.sendMessage(new TextMessage("Error: Player not found!"));
                    return;
                }

                GameSession gameSession = new GameSession();
                gameSession.setPlayers(Collections.singletonList(player));
                gameSession.setScore(score);
                gameSession.setDuration(duration);
                gameSession.setSessionTime(LocalDateTime.now());

                gameSessionRepository.save(gameSession);

                session.sendMessage(new TextMessage("Session saved!"));
            } else {
                session.sendMessage(new TextMessage("Unknown message: " + messageContent));
            }
        } catch (NumberFormatException e) {
            session.sendMessage(new TextMessage("Error: Invalid number format."));
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Error: Internal server error."));
            e.printStackTrace(); // Log lỗi để kiểm tra
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            System.out.println("Player " + username + " has logged out.");
        }
    }
}
