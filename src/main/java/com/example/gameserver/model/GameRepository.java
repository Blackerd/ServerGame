package com.example.gameserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Player, Long> {
    Player findByEmail(String email);  // This method should now work
    Optional<Player> findById(Long id);

    boolean existsByUsername(String username);

    Player findByUsername(String username);
}

