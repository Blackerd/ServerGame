package com.example.gameserver.server;

import com.example.gameserver.model.GameRepository;
import com.example.gameserver.model.GameSession;
import com.example.gameserver.model.GameSessionRepository;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    // Lưu điểm và thời gian chơi
    public void saveGameSession(Player player, int score, long duration) {
        GameSession gameSession = new GameSession();
        gameSession.setPlayer(player);
        gameSession.setScore(score);
        gameSession.setDuration(duration);
        gameSession.setSessionTime(java.time.LocalDateTime.now());

        gameSessionRepository.save(gameSession);
    }

    // Lấy danh sách bảng xếp hạng
    public List<GameSession> getLeaderboard() {
        Pageable pageable = PageRequest.of(0, 10);
        return gameSessionRepository.findTopScores(pageable);
    }

    // Lấy danh sách phiên chơi của người chơi
    public List<GameSession> getPlayerSessions(Player player) {
        return gameSessionRepository.findByPlayer(player);
    }
}
