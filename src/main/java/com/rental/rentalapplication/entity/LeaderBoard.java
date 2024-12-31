package com.rental.rentalapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leaderboards")
public class LeaderBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // UUID duy nhất cho từng bản ghi bảng xếp hạng.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người chơi liên quan.

    @Column(nullable = false)
    private int score; // Điểm số.

    @Column(nullable = false)
    private int rank; // Xếp hạng (cập nhật định kỳ).

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Lần cập nhật gần nhất.

    @Version
    private Long version; // Sử dụng để kiểm tra xem có ai cập nhật dữ liệu không.

    // Constructor không tham số để JPA sử dụng
    public LeaderBoard(User user, int score, int rank, LocalDateTime updatedAt) {
        this.user = user;
        this.score = score;
        this.rank = rank;
        this.updatedAt = updatedAt;
    }
}
