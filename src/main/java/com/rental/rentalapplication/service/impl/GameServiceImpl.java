package com.rental.rentalapplication.service.impl;

import com.rental.rentalapplication.entity.GameSession;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.repository.GameSessionRepository;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GameServiceImpl implements GameService {
    private final UserRepository userRepository;
    private final GameSessionRepository gameSessionRepository;

    public GameServiceImpl(UserRepository userRepository, GameSessionRepository gameSessionRepository) {
        this.userRepository = userRepository;
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public void saveGameSession(List<User> users, int score, long time) {
        GameSession gameSession = new GameSession();
        gameSession.setUsers(users);
        gameSession.setScore(score);
        gameSession.setDuration(time);

        gameSessionRepository.save(gameSession);

    }

    @Override
    public void getTopScores() {

    }

    @Override
    public void getGameSessionsByUserId(long userId) {

    }
}
