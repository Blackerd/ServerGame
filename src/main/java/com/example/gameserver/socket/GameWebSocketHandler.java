package com.example.gameserver.socket;


import com.example.gameserver.model.Player;
import com.example.gameserver.server.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final AuthService gameService;

    @Autowired
    public GameWebSocketHandler(AuthService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Xử lý khi kết nối WebSocket được thiết lập
        System.out.println("Player connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();

        if (messageContent.startsWith("SAVE_SESSION")) {
            // Giả sử format: "SAVE_SESSION|score|duration|username"
            String[] parts = messageContent.split("\\|");
            int score = Integer.parseInt(parts[1]);
            long duration = Long.parseLong(parts[2]);
            String username = parts[3];

            // Lấy người chơi và lưu phiên chơi
            Player player = gameService.getPlayerByUsername(username);
            if (player != null) {
                gameService.saveGameSession(player, score, duration);
                session.sendMessage(new TextMessage("Session saved!"));
            } else {
                session.sendMessage(new TextMessage("Error: Player not found!"));
            }
            return;
        }

        session.sendMessage(new TextMessage("Unknown message: " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            System.out.println("Player " + username + " has logged in.");
        }
    }

}
