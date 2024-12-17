package com.rental.rentalapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // UUID đảm bảo duy nhất cho từng user.

    @Column(nullable = false, unique = true)
    private String username; // Tên đăng nhập không trùng.

    @Column(nullable = false)
    private String password; // Mật khẩu (nên mã hóa).

    @Column(nullable = false, unique = true)
    private String email; // Email không trùng.

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles; // Nhiều quyền cho 1 người chơi.

    @Column(nullable = false)
    private boolean isOnline; // Trạng thái người chơi có đang online không.

    @Column(nullable = false)
    private int totalScore; // Tổng điểm của người chơi (nếu cần xếp hạng).

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời gian đăng ký tài khoản.

    @Column(nullable = true)
    private LocalDateTime lastLogin; // Lần đăng nhập gần nhất.
}
