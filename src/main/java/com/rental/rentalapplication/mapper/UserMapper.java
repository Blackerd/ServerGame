package com.rental.rentalapplication.mapper;


import com.rental.rentalapplication.dto.request.UserCreationRequest;
import com.rental.rentalapplication.dto.request.UserUpdateRequest;
import com.rental.rentalapplication.dto.response.UserResponse;
import com.rental.rentalapplication.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserResponse toUserResponse(User user);
}
