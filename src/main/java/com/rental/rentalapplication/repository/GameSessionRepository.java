package com.rental.rentalapplication.repository;

import com.rental.rentalapplication.entity.GameSession;
import com.rental.rentalapplication.entity.User;
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

    // lấy danh sách xếp hạng theo rank
    @Query("SELECT g FROM GameSession g ORDER BY g.rank ASC")
    List<GameSession> findTopRanks(Pageable pageable);

    // Tìm các phiên chơi của một người chơi
    List<GameSession> findByUsers(User user);

    // Tìm các phiên chơi trong một khoảng thời gian
    List<GameSession> findBySessionTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}