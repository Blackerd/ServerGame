package com.example.gameserver.server;

import com.example.gameserver.model.GameSession;
import com.example.gameserver.model.LoginRequest;
import com.example.gameserver.model.Player;
import com.example.gameserver.model.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Endpoint đăng ký
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            boolean isRegistered = authService.register(registerRequest);
            if (isRegistered) {
                return ResponseEntity.ok("Registration successful!"); // 200 OK
            } else {
                return ResponseEntity.status(409).body("Username or email already exists!"); // 409 Conflict
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during registration: " + e.getMessage()); // 500 Internal Server Error
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            boolean isLoggedIn = authService.login(loginRequest);
            if (isLoggedIn) {
                return ResponseEntity.ok("Login successful!"); // 200 OK
            }
            return ResponseEntity.status(401).body("Invalid username or password!"); // 401 Unauthorized
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
