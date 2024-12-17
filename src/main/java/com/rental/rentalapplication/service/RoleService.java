package com.rental.rentalapplication.service;


import com.rental.rentalapplication.dto.request.RoleRequest;
import com.rental.rentalapplication.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);

    RoleResponse getRole(String id);

    void delete(String id);

    RoleResponse update(String id, RoleRequest request);

    List<RoleResponse> getAll();

}
