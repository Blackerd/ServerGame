package com.rental.rentalapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    @Column(nullable = false, unique = true)
    private String name; // Sử dụng "name" làm khóa chính (VD: ADMIN, PLAYER).

    private String description; // Mô tả quyền hạn.

    @ManyToMany(mappedBy = "roles")
    private Set<User> users; // Nhiều người chơi có thể dùng vai trò này.

}
