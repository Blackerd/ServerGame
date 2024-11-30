package com.example.gameserver.socket;


import com.example.gameserver.model.*;
import com.example.gameserver.server.AuthService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
        session.getAttributes().put("loggedIn", false); // Mặc định chưa đăng nhập
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();
        JSONObject jsonMessage = new JSONObject(messageContent);
        try {
            String type = jsonMessage.getString("type");

            switch (type) {
                case "REGISTER": // Đăng ký
                    handleRegister(session, jsonMessage);
                    break;

                case "LOGIN": // Đăng nhập
                    handleLogin(session, jsonMessage);
                    break;

                case "UPDATE_SCORE": // Cập nhật điểm số
                    handleUpdateScore(session, jsonMessage);
                    break;

                case "GET_LEADERBOARD": // Lấy bảng xếp hạng điểm số
                    handleGetLeaderboard(session);
                    break;

                case "GAME_EVENT": // Sự kiện trong game
                    handleGameEvent(session, jsonMessage);
                    break;

                default:
                    sendErrorMessage(session, "Unknown type: " + type);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi chi tiết
            sendErrorMessage(session, "Internal server error.");
        }
    }

    private void handleRegister(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        String username = jsonMessage.getString("username");
        String password = jsonMessage.getString("password");
        String email = jsonMessage.getString("email");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setEmail(email);

        boolean isRegistered = authService.register(registerRequest);
        JSONObject response = new JSONObject();
        response.put("type", "REGISTER_RESPONSE");
        response.put("success", isRegistered);
        response.put("message", isRegistered ? "Registration successful" : "Registration failed. Username or email already exists.");
        session.sendMessage(new TextMessage(response.toString()));
    }

    private void handleLogin(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        String username = jsonMessage.getString("username");
        String password = jsonMessage.getString("password");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);


        boolean isLoggedIn = authService.login(loginRequest);

        JSONObject response = new JSONObject();
        response.put("type", "LOGIN_RESPONSE");
        response.put("success", isLoggedIn);
        if (isLoggedIn) {
            response.put("message", "Login successful");
            session.getAttributes().put("loggedIn", true);
            session.getAttributes().put("username", username);
        } else {
            response.put("message", "Invalid username or password");
        }
        session.sendMessage(new TextMessage(response.toString()));
    }

    private void handleUpdateScore(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        if (!(boolean) session.getAttributes().getOrDefault("loggedIn", false)) {
            sendErrorMessage(session, "You must be logged in to update your score.");
            return;
        }

        String username = (String) session.getAttributes().get("username");
        int score = jsonMessage.getInt("score");

        Player player = authService.getPlayerByUsername(username);
        if (player != null) {
            GameSession gameSession = new GameSession();

            gameSession.setPlayers(Collections.singletonList(player));
            gameSession.setScore(score);
            gameSession.setSessionTime(LocalDateTime.now());
            gameSessionRepository.save(gameSession);

            JSONObject response = new JSONObject();
            response.put("type", "UPDATE_SCORE_RESPONSE");
            response.put("success", true);
            response.put("message", "Score updated successfully.");
            session.sendMessage(new TextMessage(response.toString()));
        } else {
            sendErrorMessage(session, "Player not found.");
        }
    }


    private void handleGetLeaderboard(WebSocketSession session) throws Exception {
        List<GameSession> leaderboard = authService.getLeaderboard();
        JSONObject response = new JSONObject();
        response.put("type", "LEADERBOARD_RESPONSE");
        response.put("success", true);
        response.put("leaderboard", leaderboard);
        session.sendMessage(new TextMessage(response.toString()));
    }

    private void handleGameEvent(WebSocketSession session, JSONObject jsonMessage) throws Exception {
        if (!(boolean) session.getAttributes().getOrDefault("loggedIn", false)) {
            sendErrorMessage(session, "You must be logged in to participate in game events.");
            return;
        }
        // Xử lý các sự kiện trong game (di chuyển, hành động, trạng thái, v.v.)
        System.out.println("Game event received: " + jsonMessage.toString());
        // Phản hồi nếu cần thiết
    }


    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws Exception {
        JSONObject response = new JSONObject();
        response.put("type", "ERROR");
        response.put("success", false);
        response.put("message", errorMessage);
        session.sendMessage(new TextMessage(response.toString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            System.out.println("Player " + username + " has logged out.");
        }
    }
}