package com.example.gameserver.server;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class GameWebSocketClient extends WebSocketClient {

    public GameWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // Khi kết nối WebSocket thành công
        System.out.println("Connected to server");

        // Gửi yêu cầu START khi kết nối thành công
        send("START");

        // Sau khi gửi yêu cầu START, chờ 2 giây rồi gửi yêu cầu END
        try {
            Thread.sleep(2000); // Đợi 2 giây
            send("END"); // Gửi yêu cầu kết thúc trò chơi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        // Xử lý tin nhắn nhận được từ server
        System.out.println("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Khi kết nối WebSocket bị đóng
        System.out.println("Disconnected from server");
    }

    @Override
    public void onError(Exception ex) {
        // Xử lý khi có lỗi xảy ra
        ex.printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        // Kết nối đến server WebSocket tại địa chỉ ws://localhost:8080/game
        URI uri = new URI("ws://localhost:7000/game");
        GameWebSocketClient client = new GameWebSocketClient(uri);

        client.connectBlocking(); // Đợi đến khi kết nối thành công

        // Chờ một chút để client thực hiện các yêu cầu sau khi kết nối
        // (không cần gọi send ở đây nữa, vì đã thực hiện trong onOpen)
        Thread.sleep(4000); // Đợi đủ lâu để server phản hồi
        client.close(); // Đóng kết nối
    }
}
