package com.rental.rentalapplication.service;

import com.rental.rentalapplication.entity.User;

import java.util.List;

public interface GameService {
    // Lưu thông tin game session
    void saveGameSession(List<User> users, int score, long time);

    // Lấy danh sách xếp hạng 10 người chơi có điểm số cao nhất
    void getTopScores();

    // Lấy các phiên chơi mà người chơi tham gia theo userid
    void getGameSessionsByUserId(long userId);

}
