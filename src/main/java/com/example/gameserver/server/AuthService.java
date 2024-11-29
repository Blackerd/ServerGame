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
            private GameRepository gameRepository;

            @Autowired
            private GameSessionRepository gameSessionRepository;

            private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Hàm đăng ký người dùng
            public String register(Player player) {
                if (gameRepository.existsByUsername(player.getUsername())) {
                    return "Username already registered!";
                }

                // Mã hóa mật khẩu trước khi lưu
                String encodedPassword = passwordEncoder.encode(player.getPasswordHash());
                player.setPasswordHash(encodedPassword);

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
                if (!passwordEncoder.matches(loginRequest.getPassword(), player.getPasswordHash())) {
                    return "Invalid password!";
                }

                return "Login successful!";
            }

            // Lấy danh sách bảng xếp hạng
            public List<GameSession> getLeaderboard() {
                Pageable pageable = PageRequest.of(0, 10);
                return gameSessionRepository.findTopScores(pageable);
            }

            // Lấy người chơi theo tên đăng nhập
            public Player getPlayerByUsername(String username) {
                return gameRepository.findByUsername(username);
            }

            // Lưu phiên chơi
            public void saveGameSession(Player player, int score, long duration) {
                GameSession gameSession = new GameSession();
                gameSession.setPlayer(player);
                gameSession.setScore(score);
                gameSession.setDuration(duration);
                gameSession.setSessionTime(java.time.LocalDateTime.now());

                gameSessionRepository.save(gameSession);
            }
        }
