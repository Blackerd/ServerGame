package com.example.gameserver.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Player, Long> {
    Player findByEmail(String email);
    Player findByEmailAndPassword(String email, String password);
    Optional<Player> findById(Long id);
}
