package com.example.gameserver.server;

import com.example.gameserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Hàm đăng ký người dùng
    public String register(RegisterRequest registerRequest) {
        if (playerRepository.existsByUsername(registerRequest.getUsername())) {
            return "Username already registered!";
        }

        // Kiểm tra email đã tồn tại
        if (playerRepository.existsByEmail(registerRequest.getEmail())) {
            return "Email already registered!";
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        registerRequest.setPassword(encodedPassword);

        Player player = new Player();
        player.setUsername(registerRequest.getUsername());
        player.setEmail(registerRequest.getEmail());
        player.setPasswordHash(encodedPassword);

        // Lưu người dùng vào cơ sở dữ liệu
        playerRepository.save(player);

        return "Registration successful!";
    }

    // Hàm đăng nhập
    public String login(LoginRequest loginRequest) {
        Player player = playerRepository.findByUsername(loginRequest.getUsername());
        if (player == null) {
            return "User not found!";
        }

        // Kiểm tra mật khẩu có khớp không
        if (!passwordEncoder.matches(loginRequest.getPassword(), player.getPasswordHash())) {
            return "Invalid password!";
        }

        return "Login successful!";
    }

    // Lấy danh sách bảng xếp hạng
    public List<GameSession> getLeaderboard() {
        Pageable pageable = PageRequest.of(0, 10); // Chỉ lấy 10 phiên chơi hàng đầu
        return gameSessionRepository.findTopScores(pageable);
    }

    // Lấy người chơi theo tên đăng nhập
    public Player getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    // Lưu phiên chơi với nhiều người chơi
    public void saveGameSession(List<Player> players, int score, long duration) {
        GameSession gameSession = new GameSession();
        gameSession.setPlayers(players); // Thêm danh sách người chơi vào phiên chơi
        gameSession.setScore(score);
        gameSession.setDuration(duration);
        gameSession.setSessionTime(java.time.LocalDateTime.now());

        gameSessionRepository.save(gameSession); // Lưu vào cơ sở dữ liệu
    }
}
