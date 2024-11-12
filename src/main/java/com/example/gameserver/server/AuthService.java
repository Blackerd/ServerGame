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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // đăng ký
    public String register(Player player) {
        // Kiểm tra xem email đã tồn tại chưa
        if (gameRepository.findByEmail(player.getEmail()) != null) {
            return "Email already registered!";
        }

        // Mã hóa mật khẩu trước khi lưu
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        gameRepository.save(player);
        return "Registration successful!";
    }

    // đăng nhập
    public String login(LoginRequest loginRequest) {
        Player player = gameRepository.findByEmail(loginRequest.getEmail());
        if (player == null) {
            return "User not found!";
        }

        // Kiểm tra mật khẩu sau khi giải mã
        if (!passwordEncoder.matches(loginRequest.getPassword(), player.getPassword())) {
            return "Invalid password!";
        }

        return "Login successful!";
    }
}
