package com.rental.rentalapplication.service;

import com.rental.rentalapplication.entity.LeaderBoard;

import java.util.List;

public interface LeaderBoardService {

    /**
     * Lấy danh sách tất cả người chơi trên bảng xếp hạng
     * @return List<LeaderBoard>
     */
    List<LeaderBoard> getAllPlayers();

    /**
     * Lấy thông tin người chơi theo ID
     * @param id Long
     * @return LeaderBoard hoặc null nếu không tìm thấy
     */
    LeaderBoard getPlayerById(Long id);

    /**
     * Lưu hoặc cập nhật thông tin người chơi
     * @param leaderBoard LeaderBoard
     * @return LeaderBoard đã được lưu
     */
    LeaderBoard saveOrUpdatePlayer(LeaderBoard leaderBoard);

    /**
     * Xóa người chơi khỏi bảng xếp hạng theo ID
     * @param id Long
     */
    void deletePlayer(Long id);
}
