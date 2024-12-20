package com.rental.rentalapplication.repository;

import com.rental.rentalapplication.entity.LeaderBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderBoardRepository extends JpaRepository<LeaderBoard, UUID> {
    // JpaRepository đã hỗ trợ các phương thức như findAll(), findById(), save(), delete(), v.v.

    Optional<LeaderBoard> findByUserId(UUID userId);

    // Lấy tất cả LeaderBoard đã được sắp xếp theo rank
    List<LeaderBoard> findAllByOrderByRankAsc();
}
