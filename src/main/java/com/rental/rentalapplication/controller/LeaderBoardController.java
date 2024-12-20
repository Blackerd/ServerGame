package com.rental.rentalapplication.controller;

import com.rental.rentalapplication.dto.response.LeaderBoardResponse;
import com.rental.rentalapplication.entity.LeaderBoard;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    /**
     * Lấy danh sách tất cả người chơi trên bảng xếp hạng
     * @return List<LeaderBoardResponse>
     */
    @GetMapping("/players")
    public ResponseEntity<List<LeaderBoardResponse>> getAllPlayers() {
        List<LeaderBoardResponse> players = leaderBoardService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    /**
     * Lấy thông tin người chơi theo ID bảng xếp hạng
     * @param id UUID
     * @return LeaderBoardResponse hoặc 404 nếu không tìm thấy
     */
    @GetMapping("/player/{id}")
    public ResponseEntity<LeaderBoardResponse> getPlayerById(@PathVariable UUID id) {
        try {
            LeaderBoardResponse player = leaderBoardService.getUserRank(id);
            return ResponseEntity.ok(player);
        } catch (CustomException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lưu hoặc cập nhật thông tin người chơi
     * @param leaderBoard LeaderBoard
     * @return LeaderBoardResponse đã được lưu
     */
    @PostMapping("/player")
    public ResponseEntity<LeaderBoardResponse> saveOrUpdatePlayer(@RequestBody LeaderBoard leaderBoard) {
        LeaderBoard savedPlayer = leaderBoardService.saveOrUpdatePlayer(leaderBoard);
        LeaderBoardResponse response = LeaderBoardResponse.builder()
                .userId(savedPlayer.getUser().getId())
                .email(savedPlayer.getUser().getEmail())
                .score(savedPlayer.getScore())
                .rank(savedPlayer.getRank())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa người chơi khỏi bảng xếp hạng theo ID
     * @param id UUID
     * @return 204 No Content nếu thành công, 400 Bad Request nếu thất bại
     */
    @DeleteMapping("/player/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        try {
            leaderBoardService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (CustomException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
