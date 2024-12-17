package com.rental.rentalapplication.repository;

import com.rental.rentalapplication.entity.LeaderBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderBoardRepository extends JpaRepository<LeaderBoard, Long> {
    // JpaRepository đã hỗ trợ các phương thức như findAll(), findById(), save(), delete(), v.v.
}
