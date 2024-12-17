package com.rental.rentalapplication.repository;

import com.rental.rentalapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Tìm người dùng theo username
    Optional<User> findByUsername(String username);

    // Kiểm tra xem người dùng có tồn tại không
    boolean existsByUsername(String username);
}
