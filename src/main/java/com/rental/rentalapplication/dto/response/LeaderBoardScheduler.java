package com.rental.rentalapplication.dto.response;

import com.rental.rentalapplication.service.LeaderBoardService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderBoardScheduler {

    private final LeaderBoardService leaderBoardService;

    @Transactional
    @Scheduled(cron = "0 0 * * * ?") // Chạy mỗi giờ
    public void updateLeaderBoard() {
        leaderBoardService.getAllPlayers();
        log.info("LeaderBoard đã được cập nhật");
    }
}

