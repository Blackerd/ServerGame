package com.rental.rentalapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    private String email; // Tên đăng nhập không trùng.

    @Column(nullable = false)
    private String password; // Mật khẩu (nên mã hóa).

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles; // Nhiều quyền cho 1 người chơi.

    @Column(nullable = true)
    private boolean isOnline; // Trạng thái người chơi có đang online không.

    @Column(nullable = true)
    private int totalScore; // Tổng điểm của người chơi (nếu cần xếp hạng).

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private boolean active = true; // người dùng mặc định active khi tạo

    @Column(nullable = false)
    private boolean blocked = false; // người dùng mặc định không bị chặn


}
