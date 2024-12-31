package com.rental.rentalapplication.service.impl;

import com.rental.rentalapplication.dto.response.LeaderBoardResponse;
import com.rental.rentalapplication.entity.LeaderBoard;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.exception.Error;
import com.rental.rentalapplication.repository.LeaderBoardRepository;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.LeaderBoardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private final LeaderBoardRepository leaderBoardRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public List<LeaderBoardResponse> getAllPlayers() {
        List<LeaderBoard> leaderBoards = leaderBoardRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(LeaderBoard::getScore).reversed())
                .collect(Collectors.toList());

        // Cập nhật xếp hạng
        int rank = 1;
        for (LeaderBoard lb : leaderBoards) {
            lb.setRank(rank++);
            lb.setUpdatedAt(LocalDateTime.now());
            leaderBoardRepository.save(lb);
        }

        return leaderBoards.stream()
                .map(lb -> LeaderBoardResponse.builder()
                        .userId(lb.getUser().getId())
                        .email(lb.getUser().getEmail())
                        .score(lb.getScore())
                        .rank(lb.getRank())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public LeaderBoard getPlayerById(UUID id) {
        Optional<LeaderBoard> player = leaderBoardRepository.findById(id);
        return player.orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
    }

    @Override
    @Transactional
    public LeaderBoard saveOrUpdatePlayer(LeaderBoard leaderBoard) {
        // Save or update the leaderBoard entry
        LeaderBoard savedLeaderBoard = leaderBoardRepository.save(leaderBoard);

        // Recalculate ranks after saving
        recalculateRanks();

        return savedLeaderBoard;
    }
    /**
     * Recalculate ranks for all players based on their scores.
     * Players with higher scores have better (lower) ranks.
     */
    @Override
    @Transactional
    public void recalculateRanks() {
        // Fetch all leaderboard entries sorted by score descending
        List<LeaderBoard> leaderBoards = leaderBoardRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));

        int rank = 1;
        for (LeaderBoard leaderBoard : leaderBoards) {
            leaderBoard.setRank(rank++);
            leaderBoard.setUpdatedAt(LocalDateTime.now());
            leaderBoardRepository.save(leaderBoard);
        }
    }

    @Transactional
    @Override
    public void deletePlayer(UUID id) {
        if (leaderBoardRepository.existsById(id)) {
            leaderBoardRepository.deleteById(id);
        } else {
            throw new CustomException(Error.USER_NOT_EXISTED);
        }
    }


    @Transactional
    @Override
    @Cacheable(value = "leaderboard", key = "#userId")
    public LeaderBoardResponse getUserRank(UUID userId) {
        Optional<LeaderBoard> optionalLeaderBoard = leaderBoardRepository.findByUserId(userId);
        if (optionalLeaderBoard.isEmpty()) {
            throw new CustomException(Error.USER_NOT_EXISTED);
        }
        LeaderBoard leaderBoard = optionalLeaderBoard.get();
        return convertToResponse(leaderBoard);
    }

    @Override
    @Transactional
    @CacheEvict(value = "leaderboard", allEntries = true)
    public LeaderBoardResponse updateUserScore(UUID userId, int score) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));

        // Find existing leaderboard entry or create new one
        LeaderBoard leaderBoard = leaderBoardRepository.findByUserId(userId)
                .orElseGet(() -> new LeaderBoard(user, 0, 0, LocalDateTime.now()));

        // Update score and save
        leaderBoard.setScore(leaderBoard.getScore() + score);
        leaderBoard.setUpdatedAt(LocalDateTime.now());
        leaderBoardRepository.save(leaderBoard);

        // Recalculate ranks
        recalculateRanks();

        return convertToResponse(leaderBoard);
    }

    @Transactional
    @Override
    public List<LeaderBoardResponse> getAllLeaderBoards() {
        // Fetch all leaderboards sorted by rank
        List<LeaderBoard> leaderBoards = leaderBoardRepository.findAll(Sort.by(Sort.Direction.ASC, "rank"));
        return leaderBoards.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    /**
     * Convert LeaderBoard entity to LeaderBoardResponse DTO
     */
    private LeaderBoardResponse convertToResponse(LeaderBoard leaderBoard) {
        return LeaderBoardResponse.builder()
                .userId(leaderBoard.getUser().getId())
                .email(leaderBoard.getUser().getEmail())
                .score(leaderBoard.getScore())
                .rank(leaderBoard.getRank())
                .updatedAt(leaderBoard.getUpdatedAt())
                .build();
    }
}
