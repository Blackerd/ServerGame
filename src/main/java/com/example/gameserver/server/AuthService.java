package com.example.gameserver.server;

import com.example.gameserver.model.GameRepository;
import com.example.gameserver.model.LoginRequest;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private GameRepository gameRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Hàm đăng ký người dùng
    public String register(Player player) {
        if (gameRepository.existsByUsername(player.getUsername())) {
            return "Username already registered!";
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(player.getPassword());
        player.setPassword(encodedPassword);

        // Lưu người dùng vào cơ sở dữ liệu
        gameRepository.save(player);

        return "Registration successful!";
    }

    // Hàm đăng nhập
    public String login(LoginRequest loginRequest) {
        Player player = gameRepository.findByUsername(loginRequest.getUsername());
        if (player == null) {
            return "User not found!";
        }

        // Kiểm tra mật khẩu có khớp không
        if (!passwordEncoder.matches(loginRequest.getPassword(), player.getPassword())) {
            return "Invalid password!";
        }

        return "Login successful!";
    }
}
