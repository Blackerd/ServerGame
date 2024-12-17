package com.rental.rentalapplication.service.impl;

import com.rental.rentalapplication.entity.GameSession;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.repository.GameSessionRepository;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class GameServiceImpl implements GameService {
    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;

    public GameServiceImpl(UserRepository userRepository, GameSessionRepository gameSessionRepository) {
        this.userRepository = userRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public void saveGameSession(List<User> users, int score, long time) {

        // Kiểm tra danh sách người chơi
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("Users list cannot be null or empty.");
        }

        // Lấy thời gian hiện tại làm startTime
        LocalDateTime startTime = LocalDateTime.now();

        GameSession gameSession = new GameSession();
        gameSession.setUsers(users);
        gameSession.setScore(score);
        gameSession.setDuration(time);
        gameSession.setStartTime(startTime);
        gameSession.setStatus("IN_PROGRESS");

        gameSessionRepository.save(gameSession);

    }

    @Override
    public List<GameSession> getTopScores() {
        // Truy xuất danh sách top 10
        return gameSessionRepository.findTopScores(Pageable.ofSize(10));
    }


    @Override
    public List<GameSession> getGameSessionsByUserId(UUID userId) {
        User user = userRepository.findById(userId.toString()).orElseThrow(() -> new RuntimeException("User not found"));
        return gameSessionRepository.findByUser(user);
    }


}
