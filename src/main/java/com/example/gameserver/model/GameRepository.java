package com.example.gameserver.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Player, Long> {
    Player findByEmail(String email);  // This method should now work
    Optional<Player> findById(Long id);

    boolean existsByUsername(String username);

    Player findByUsername(String username);
}

