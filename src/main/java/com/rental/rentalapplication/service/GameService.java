package com.rental.rentalapplication.service;

import com.rental.rentalapplication.entity.GameSession;
import com.rental.rentalapplication.entity.User;

import java.util.List;
import java.util.UUID;

public interface GameService {
    // Lưu thông tin game session
    void saveGameSession(List<User> users, int score, long time);

    // Lấy danh sách xếp hạng 10 người chơi có điểm số cao nhất
    List<GameSession> getTopScores();

    // Lấy các phiên chơi mà người chơi tham gia theo UUID
    List<GameSession> getGameSessionsByUserId(UUID userId);

}
