package com.example.gameserver.server;

import com.example.gameserver.model.GameSession;
import com.example.gameserver.model.GameSessionRepository;
import com.example.gameserver.model.Player;
import com.example.gameserver.model.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    // Lưu điểm và thời gian chơi cho một game session, với nhiều người chơi
    public void saveGameSession(List<Player> players, int score, long duration) {
        GameSession gameSession = new GameSession();
        gameSession.setPlayers(players);  // Lưu nhiều người chơi
        gameSession.setScore(score);      // Điểm của phiên chơi
        gameSession.setDuration(duration); // Thời gian chơi
        gameSession.setSessionTime(java.time.LocalDateTime.now());  // Thời gian bắt đầu

        gameSessionRepository.save(gameSession);  // Lưu vào CSDL
    }

    // Lấy danh sách bảng xếp hạng top 10 người chơi
    public List<GameSession> getLeaderboard() {
        Pageable pageable = PageRequest.of(0, 10);  // Lấy 10 game session có điểm cao nhất
        return gameSessionRepository.findTopScores(pageable);
    }

    // Lấy tất cả các phiên chơi của một người chơi
    public List<GameSession> getPlayerSessions(Player player) {
        return gameSessionRepository.findByPlayer(player);  // Lấy game session của người chơi
    }

    // Lấy các phiên chơi mà người chơi tham gia (theo player ID)
    public List<GameSession> getSessionsByPlayerId(Long playerId) {
        return gameSessionRepository.findSessionsByPlayerId(playerId);  // Truy vấn game session theo playerId
    }

    // Tìm các phiên chơi trong một khoảng thời gian
    public List<GameSession> getSessionsByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        return gameSessionRepository.findBySessionTimeBetween(startTime, endTime);  // Truy vấn theo khoảng thời gian
    }
}
