package com.example.gameserver.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @Query("SELECT g FROM GameSession g ORDER BY g.score DESC")
    List<GameSession> findTopScores(Pageable pageable); // Lấy danh sách xếp hạng theo điểm số

    List<GameSession> findByPlayer(Player player); // Lấy các phiên chơi của một người chơi
}
