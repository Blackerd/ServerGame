package com.rental.rentalapplication.repository;

import com.rental.rentalapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    long countByIsOnlineTrue();

    // Sử dụng projection để lấy các trường cần thiết
    @Query("SELECT u.id as id, u.email as email, r.name as role " +
            "FROM User u JOIN u.roles r WHERE u.isOnline = true")
    List<OnlineUserProjection> findOnlineUsers();

    interface OnlineUserProjection {
        UUID getId();
        String getEmail();
        String getRole();
    }

}
