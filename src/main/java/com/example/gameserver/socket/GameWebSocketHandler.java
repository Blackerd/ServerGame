package com.example.gameserver.socket;


import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Xử lý khi kết nối WebSocket được thiết lập
        System.out.println("Player connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageContent = message.getPayload();
        if (messageContent.equals("START")) {
            // Xử lý khi nhận được yêu cầu bắt đầu trò chơi từ client
            System.out.println("Game started: " + session.getId());
            session.sendMessage(new TextMessage("Game started"));
            return;
        } else if (messageContent.equals("END")) {
            // Xử lý khi nhận được yêu cầu kết thúc trò chơi từ client
            System.out.println("Game ended: " + session.getId());
            session.sendMessage(new TextMessage("Game ended"));
            return;
        }

        // Trả lời lại client
        session.sendMessage(new TextMessage("Game message: " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Xử lý khi kết nối WebSocket bị đóng
        System.out.println("Player disconnected: " + session.getId());
    }

}
