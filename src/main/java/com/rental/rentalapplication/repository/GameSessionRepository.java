package com.rental.rentalapplication.repository;

import com.rental.rentalapplication.entity.GameSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {
    // Lấy danh sách xếp hạng theo điểm số
    @Query("SELECT g FROM GameSession g ORDER BY g.score DESC")
    List<GameSession> findTopScores(Pageable pageable);

    // Tìm các phiên chơi của một người chơi (sử dụng UUID cho user)
    @Query("SELECT g FROM GameSession g WHERE g.user.id = :userId")
    List<GameSession> findByUserId(UUID userId);

    // Tìm các phiên chơi trong một khoảng thời gian
    @Query("SELECT g FROM GameSession g WHERE g.startTime >= :startTime AND g.endTime <= :endTime")
    List<GameSession> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
