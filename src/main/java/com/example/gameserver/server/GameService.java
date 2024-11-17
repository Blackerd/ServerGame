package com.example.gameserver.server;

import com.example.gameserver.model.GameRepository;
import com.example.gameserver.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    // luuđiểm
    public void savePlayerScore(Player player) {
        gameRepository.save(player);
    }

    public Player getPlayerByEmail(String email) {
        return gameRepository.findByEmail(email);
    }

    // lấy thông tin user qua id
    public Player getPlayerById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

}
