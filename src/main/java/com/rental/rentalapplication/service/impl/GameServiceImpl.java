package com.rental.rentalapplication.service.impl;

import com.rental.rentalapplication.entity.GameSession;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.exception.Error;
import com.rental.rentalapplication.repository.GameSessionRepository;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.GameService;
import com.rental.rentalapplication.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;
    private final LeaderBoardService leaderBoardService;

    @Transactional
    @Override
    public void saveGameSession(User user, int score, long time) {
        // Kiểm tra người chơi không null
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        // Lấy thời gian hiện tại làm startTime và endTime
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusSeconds(time);

        GameSession gameSession = new GameSession();
        gameSession.setUser(user);
        gameSession.setScore(score);
        gameSession.setDuration(time);
        gameSession.setStartTime(startTime);
        gameSession.setEndTime(endTime);
        gameSession.setStatus("FINISHED"); // Giả sử phiên chơi đã kết thúc

        // Lưu GameSession
        gameSessionRepository.save(gameSession);

        // Cập nhật tổng điểm cho user
        user.setTotalScore(user.getTotalScore() + score);
        userRepository.save(user);

        // Cập nhật LeaderBoard cho người chơi
        leaderBoardService.updateUserScore(user.getId(), score);
    }

    @Transactional
    @Override
    public List<GameSession> getTopScores() {
        // Truy xuất danh sách top 10
        return gameSessionRepository.findTopScores(Pageable.ofSize(10));
    }

    @Transactional
    @Override
    public List<GameSession> getGameSessionsByUserId(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));

        // Lấy danh sách game sessions
        return gameSessionRepository.findByUserId(userId);
    }
}
