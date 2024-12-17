package com.rental.rentalapplication.controller;

import com.rental.rentalapplication.entity.LeaderBoard;
import com.rental.rentalapplication.service.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderBoardController {

    @Autowired
    private LeaderBoardService leaderBoardService;

    @GetMapping("/players")
    public ResponseEntity<List<LeaderBoard>> getAllPlayers() {
        return ResponseEntity.ok(leaderBoardService.getAllPlayers());
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<LeaderBoard> getPlayerById(@PathVariable Long id) {
        LeaderBoard player = leaderBoardService.getPlayerById(id);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/player")
    public ResponseEntity<LeaderBoard> saveOrUpdatePlayer(@RequestBody LeaderBoard leaderBoard) {
        LeaderBoard savedPlayer = leaderBoardService.saveOrUpdatePlayer(leaderBoard);
        return ResponseEntity.ok(savedPlayer);
    }

    @DeleteMapping("/player/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        try {
            leaderBoardService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
