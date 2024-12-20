package com.rental.rentalapplication.service;

import com.rental.rentalapplication.dto.request.UserCreationRequest;
import com.rental.rentalapplication.dto.request.UserUpdateRequest;
import com.rental.rentalapplication.dto.response.OnlineUserDTO;
import com.rental.rentalapplication.dto.response.SystemStatsResponse;
import com.rental.rentalapplication.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    List<UserResponse> getUsers();

    @PreAuthorize("hasRole('ADMIN')")
    UserResponse getUserById(UUID userId);

    // Lấy thông tin user theo email, dùng @PostAuthorize ở implementation
    UserResponse getUserByEmail(String email);

    UserResponse getMyInfo();

    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(UUID userId);

    @PreAuthorize("hasRole('ADMIN')")
    Page<UserResponse> getAllUsers(Integer pageNo, Integer pageSize);

    @PreAuthorize("hasRole('ADMIN')")
    UserResponse updateUserByAdmin(UUID id, UserUpdateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void deactivateUser(UUID userId);

    @PreAuthorize("hasRole('ADMIN')")
    void activateUser(UUID userId);

    @PreAuthorize("hasRole('ADMIN')")
    void blockUser(UUID userId);

    @PreAuthorize("hasRole('ADMIN')")
    void unblockUser(UUID userId);

    @PreAuthorize("hasRole('ADMIN')")
    long countOnlineUsers();

    @PreAuthorize("hasRole('ADMIN')")
    SystemStatsResponse getSystemStats();

    @PreAuthorize("hasRole('ADMIN')")
    void reloadServerConfig();

    @PreAuthorize("hasRole('ADMIN')")
    void enterMaintenanceMode();

    @PreAuthorize("hasRole('ADMIN')")
    void exitMaintenanceMode();

    @PreAuthorize("hasRole('ADMIN')")
    List<OnlineUserDTO> getOnlineUserDetails();

    void setUserOnline(String email, boolean isOnline);


}
