package com.rental.rentalapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // UUID duy nhất cho phiên chơi.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người chơi duy nhất trong phiên chơi.

    @Column(nullable = false)
    private int score; // Tổng điểm của phiên chơi.

    @Column(nullable = false)
    private long duration; // Thời lượng chơi (giây).

    @Column(nullable = false)
    private LocalDateTime startTime; // Thời gian bắt đầu.

    @Column(nullable = true)
    private LocalDateTime endTime; // Thời gian kết thúc (nếu cần).

    @Column(nullable = false)
    private String status; // Trạng thái (VD: IN_PROGRESS, FINISHED).
}
