package com.rental.rentalapplication.service.impl;

import com.rental.rentalapplication.dto.request.UserCreationRequest;
import com.rental.rentalapplication.dto.request.UserUpdateRequest;
import com.rental.rentalapplication.dto.response.OnlineUserDTO;
import com.rental.rentalapplication.dto.response.SystemStatsResponse;
import com.rental.rentalapplication.dto.response.UserResponse;
import com.rental.rentalapplication.entity.Role;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.exception.Error;
import com.rental.rentalapplication.mapper.UserMapper;
import com.rental.rentalapplication.repository.RoleRepository;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    private boolean maintenanceMode = false;


    @Override
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(Error.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        Role role = roleRepository.findById("USER").orElseThrow(() -> new CustomException(Error.ROLE_NOT_FOUND));
        roles.add(role);
        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getUsers() {
        log.info("In method get users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.email == authentication.name")
    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            throw new CustomException(Error.UNAUTHENTICATED);
        }
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(Error.USER_NOT_EXISTED);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Integer pageNo, Integer pageSize) {
        log.info("Fetching all users: pageNo={}, pageSize={}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        var users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserResponse);
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserByAdmin(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        user.setActive(false);
        userRepository.save(user);
        log.info("User {} has been deactivated", userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        user.setActive(true);
        userRepository.save(user);
        log.info("User {} has been activated", userId);

    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        user.setBlocked(true);
        userRepository.save(user);
        log.info("User {} has been blocked", userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void unblockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        user.setBlocked(false);
        userRepository.save(user);
        log.info("User {} has been unblocked", userId);

    }

    @Override
    public long countOnlineUsers() {
        return userRepository.countByIsOnlineTrue();
    }

    @Override
    public SystemStatsResponse getSystemStats() {
        return SystemStatsResponse.builder()
                .onlineUsers(countOnlineUsers())
                .serverTime(LocalDateTime.now())
                .maintenanceMode(maintenanceMode)
                .cpuUsage(25.5)
                .memoryUsage(60.2)
                .build();
    }

    @Override
    public void reloadServerConfig() {
        log.info("Server config reloaded by admin");
    }

    @Override
    public void enterMaintenanceMode() {
        log.info("Maintenance mode enabled by admin");
    }

    @Override
    public void exitMaintenanceMode() {
        log.info("Maintenance mode disabled by admin");
    }

    @Override
    public List<OnlineUserDTO> getOnlineUserDetails() {
        List<UserRepository.OnlineUserProjection> projections = userRepository.findOnlineUsers();

        // Group các roles theo userId
        return projections.stream()
                .collect(Collectors.groupingBy(UserRepository.OnlineUserProjection::getId))
                .entrySet()
                .stream()
                .map(entry -> {
                    UUID userId = entry.getKey();
                    String email = entry.getValue().get(0).getEmail();
                    Set<String> roles = entry.getValue().stream()
                            .map(UserRepository.OnlineUserProjection::getRole)
                            .collect(Collectors.toSet());

                    return OnlineUserDTO.builder()
                            .id(userId)
                            .email(email)
                            .roles(roles)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void setUserOnline(String email, boolean isOnline) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        user.setOnline(isOnline);
        userRepository.save(user);
    }


}
