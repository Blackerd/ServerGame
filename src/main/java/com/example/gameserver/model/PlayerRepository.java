package com.example.gameserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository  extends JpaRepository<Player, Long> {
    Player findByEmail(String email);  // This method should now work
    Optional<Player> findById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Player findByUsername(String username);
}

