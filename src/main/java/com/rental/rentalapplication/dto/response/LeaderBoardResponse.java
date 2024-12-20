package com.rental.rentalapplication.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LeaderBoardResponse {
    private UUID userId;
    private String email;
    private int score;
    private int rank;
    private LocalDateTime updatedAt;
}
