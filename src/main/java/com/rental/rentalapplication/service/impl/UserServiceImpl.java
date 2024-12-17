package com.rental.rentalapplication.service.impl;



import com.rental.rentalapplication.dto.request.UserCreationRequest;
import com.rental.rentalapplication.dto.request.UserUpdateRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;


    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse getUserById(String userId) {
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED)));
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String username) {
        return userMapper.toUserResponse(userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        if (context == null) {
            throw new CustomException(Error.UNAUTHENTICATED);
        }
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Page<UserResponse> getAllUsers(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        var users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse updateUserByAdmin(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }



}