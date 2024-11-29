package com.example.gameserver.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    // Lấy danh sách xếp hạng theo điểm số
    @Query("SELECT g FROM GameSession g ORDER BY g.score DESC")
    List<GameSession> findTopScores(Pageable pageable);

    // Lấy các phiên chơi của một người chơi
    List<GameSession> findByPlayer(Player player);

    // Lấy tất cả các phiên chơi mà một người chơi tham gia (có thể là nhiều người chơi trong một phiên)
    @Query("SELECT gs FROM GameSession gs JOIN gs.player p WHERE p.id = :playerId")
    List<GameSession> findSessionsByPlayerId(Long playerId);

    // Tìm các phiên chơi trong một khoảng thời gian
    List<GameSession> findBySessionTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
