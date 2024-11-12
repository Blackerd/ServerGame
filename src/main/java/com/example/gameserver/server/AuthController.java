package com.example.gameserver.server;

import com.example.gameserver.model.LoginRequest;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // xử lý đăng ky
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Player player) {
        String result = authService.register(player);
        if (result.equals("Email already registered!")) {
            return ResponseEntity.status(409).body(result);
        }
        return ResponseEntity.ok(result);
    }
    // xử lý đăng nhập
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String result = authService.login(loginRequest);
        if (result.equals("Login successful!")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(401).body(result);
    }
}
