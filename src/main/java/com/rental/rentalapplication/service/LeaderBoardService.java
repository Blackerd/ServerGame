package com.rental.rentalapplication.service;

import com.rental.rentalapplication.dto.response.LeaderBoardResponse;
import com.rental.rentalapplication.entity.LeaderBoard;

import java.util.List;
import java.util.UUID;

public interface LeaderBoardService {

    /**
     * Lấy danh sách tất cả người chơi trên bảng xếp hạng
     * @return List<LeaderBoardResponse>
     */
    List<LeaderBoardResponse> getAllPlayers();

    /**
     * Lấy thông tin người chơi theo ID bảng xếp hạng
     * @param id UUID
     * @return LeaderBoard hoặc null nếu không tìm thấy
     */
    LeaderBoard getPlayerById(UUID id);

    /**
     * Lưu hoặc cập nhật thông tin người chơi
     * @param leaderBoard LeaderBoard
     * @return LeaderBoard đã được lưu
     */
    LeaderBoard saveOrUpdatePlayer(LeaderBoard leaderBoard);

    /**
     * Xóa người chơi khỏi bảng xếp hạng theo ID
     * @param id UUID
     */
    void deletePlayer(UUID id);

    /**
     * Lấy xếp hạng của người dùng theo userId
     * @param userId UUID
     * @return LeaderBoardResponse chứa thông tin xếp hạng
     */
    LeaderBoardResponse getUserRank(UUID userId);

    /**
     * Updates the user's score based on the game result.
     *
     * @param userId The UUID of the user.
     * @param score  The score obtained from the game.
     * @return The updated LeaderBoardResponse.
     */
    LeaderBoardResponse updateUserScore(UUID userId, int score);

    List<LeaderBoardResponse> getAllLeaderBoards();

    void recalculateRanks();
}
