package com.rental.rentalapplication.service.impl;

import com.rental.rentalapplication.entity.LeaderBoard;
import com.rental.rentalapplication.repository.LeaderBoardRepository;
import com.rental.rentalapplication.service.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaderBoardServiceImpl implements LeaderBoardService {

    @Autowired
    private LeaderBoardRepository leaderBoardRepository;

    @Override
    public List<LeaderBoard> getAllPlayers() {
        return leaderBoardRepository.findAll();
    }

    @Override
    public LeaderBoard getPlayerById(Long id) {
        Optional<LeaderBoard> player = leaderBoardRepository.findById(id);
        return player.orElse(null);
    }

    @Override
    public LeaderBoard saveOrUpdatePlayer(LeaderBoard leaderBoard) {
        return leaderBoardRepository.save(leaderBoard);
    }

    @Override
    public void deletePlayer(Long id) {
        if (leaderBoardRepository.existsById(id)) {
            leaderBoardRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Người chơi với ID " + id + " không tồn tại.");
        }
    }
}
