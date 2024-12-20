package com.rental.rentalapplication.controller;

import com.rental.rentalapplication.dto.request.UserCreationRequest;
import com.rental.rentalapplication.dto.request.UserUpdateRequest;
import com.rental.rentalapplication.dto.response.UserResponse;
import com.rental.rentalapplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Tạo mới người dùng.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Lấy danh sách tất cả người dùng (Chỉ Admin mới được truy cập).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Lấy thông tin người dùng theo ID (Chỉ Admin mới được truy cập).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Lấy thông tin chi tiết của người dùng hiện tại (Đã đăng nhập).
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        UserResponse myInfo = userService.getMyInfo();
        return ResponseEntity.ok(myInfo);
    }

    /**
     * Xóa người dùng theo ID (Chỉ Admin mới được truy cập).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cập nhật thông tin người dùng bởi Admin.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateUserByAdmin(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Phân trang và lấy danh sách người dùng (Chỉ Admin mới được truy cập).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/paged")
    public ResponseEntity<Page<UserResponse>> getPagedUsers(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<UserResponse> pagedUsers = userService.getAllUsers(pageNo, pageSize);
        return ResponseEntity.ok(pagedUsers);
    }
}
