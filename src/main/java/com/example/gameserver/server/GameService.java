package com.example.gameserver.server;

import com.example.gameserver.model.GameRepository;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public void savePlayerScore(Player player) {
        gameRepository.save(player);
    }

    public Player getPlayerByEmail(String email) {
        return gameRepository.findByEmail(email);
    }
    public void updatePlayerScore(Player player) {
        Player playerInDb = gameRepository.findById(player.getId()).orElse(null);
        if (playerInDb != null) {
            playerInDb.setScore(player.getScore());
            gameRepository.save(playerInDb);
        }
    }
}
