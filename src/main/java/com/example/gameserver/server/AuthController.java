package com.example.gameserver.server;

import com.example.gameserver.model.GameSession;
import com.example.gameserver.model.LoginRequest;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint for user registration.
     *
     * @param player The player object containing registration details.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Player player) {
        try {
            String result = authService.register(player);
            if ("Email already registered!".equals(result)) {
                return ResponseEntity.status(409).body(result); // 409 Conflict
            }
            return ResponseEntity.ok(result); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during registration: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    /**
     * Endpoint for user login.
     *
     * @param loginRequest The login request object containing email and password.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            String result = authService.login(loginRequest);
            if ("Login successful!".equals(result)) {
                return ResponseEntity.ok(result); // 200 OK
            }
            return ResponseEntity.status(401).body(result); // 401 Unauthorized
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during login: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    // Lấy danh sách bảng xếp hạng
    @GetMapping("/leaderboard")
    public List<GameSession> getLeaderboard(){
        return authService.getLeaderboard();
    }
}
