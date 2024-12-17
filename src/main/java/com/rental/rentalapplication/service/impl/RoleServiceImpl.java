package com.rental.rentalapplication.service.impl;


import com.rental.rentalapplication.dto.request.RoleRequest;
import com.rental.rentalapplication.dto.response.RoleResponse;
import com.rental.rentalapplication.entity.Role;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.exception.Error;
import com.rental.rentalapplication.mapper.RoleMapper;
import com.rental.rentalapplication.repository.RoleRepository;
import com.rental.rentalapplication.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsById(request.getName())) {
            throw new CustomException(Error.ROLE_EXISTED);
        }
        Role role = roleMapper.toRole(request);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public RoleResponse getRole(String id) {
        return roleMapper.toRoleResponse(roleRepository.findById(id).orElseThrow(() -> new CustomException(Error.ROLE_NOT_FOUND)));
    }

    public void delete(String id) {
        roleRepository.deleteById(id);
    }

    public RoleResponse update(String id, RoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new CustomException(Error.ROLE_NOT_FOUND));
        role.setName(request.getName());
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }
}