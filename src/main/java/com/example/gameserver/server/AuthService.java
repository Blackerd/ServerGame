package com.example.gameserver.server;

import com.example.gameserver.model.GameRepository;
import com.example.gameserver.model.LoginRequest;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private GameRepository gameRepository;


    public String register(Player player) {
        // Kiểm tra xem email đã tồn tại chưa
        if (gameRepository.findByEmail(player.getEmail()) != null) {
            return "Email already registered!";
        }

        // Lưu người chơi với mật khẩu chưa mã hóa
        gameRepository.save(player);
        return "Registration successful!";
    }
    public String login(LoginRequest loginRequest) {
        // Log để kiểm tra giá trị loginRequest
        System.out.println("Email: " + loginRequest.getEmail());
        System.out.println("Password: " + loginRequest.getPassword());

        // Tìm người chơi theo email
        Player player = gameRepository.findByEmail(loginRequest.getEmail());
        if (player == null) {
            return "User not found!";
        }

        // Kiểm tra mật khẩu trực tiếp
        if (!player.getPassword().equals(loginRequest.getPassword())) {
            return "Invalid password!";
        }

        return "Login successful!";
    }

}
