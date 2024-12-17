package com.rental.rentalapplication.service;


import com.rental.rentalapplication.dto.request.UserCreationRequest;
import com.rental.rentalapplication.dto.request.UserUpdateRequest;
import com.rental.rentalapplication.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    List<UserResponse> getUsers();

    UserResponse getUserById(String userId);

    UserResponse getUser(String username);

    UserResponse getMyInfo();

    void deleteUser(String userId);

    Page<UserResponse> getAllUsers(Integer pageNo, Integer pageSize);

    UserResponse updateUserByAdmin(String id, UserUpdateRequest request);

}