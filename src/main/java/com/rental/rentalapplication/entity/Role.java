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
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // ID cho vai trò.

    @Column(nullable = false, unique = true)
    private String name; // Tên vai trò (VD: ADMIN, PLAYER).

    private String description; // Mô tả quyền hạn.

    @ManyToMany(mappedBy = "roles")
    private Set<User> users; // Nhiều người chơi có thể dùng vai trò này.
}
