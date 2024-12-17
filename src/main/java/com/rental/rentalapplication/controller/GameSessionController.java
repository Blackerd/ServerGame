package com.rental.rentalapplication.controller;

import com.rental.rentalapplication.dto.request.SaveGameSessionRequest;
import com.rental.rentalapplication.entity.GameSession;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.GameService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/game-sessions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GameSessionController {
    GameService gameService;
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> saveGameSession(@RequestBody SaveGameSessionRequest request) {
        // Chuyển đổi danh sách userIds thành UUID
        List<UUID> userIds = request.getUserIds();
        List<User> users = userRepository.findAllById(userIds.stream().map(UUID::toString).toList());
        if (users.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user IDs");
        }

        // Lưu phiên chơi
        gameService.saveGameSession(users, request.getScore(), request.getDuration());
        return ResponseEntity.ok("Game session saved successfully");
    }


    @GetMapping("/top-scores")
    public ResponseEntity<List<GameSession>> getTopScores() {
        List<GameSession> topScores = gameService.getTopScores();
        return ResponseEntity.ok(topScores);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GameSession>> getGameSessionsByUserId(@PathVariable UUID userId) {
        List<GameSession> gameSessions = gameService.getGameSessionsByUserId(userId);
        return ResponseEntity.ok(gameSessions);
    }

}
