package com.rental.rentalapplication.mapper;


import com.rental.rentalapplication.dto.request.RoleRequest;
import com.rental.rentalapplication.dto.response.RoleResponse;
import com.rental.rentalapplication.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
