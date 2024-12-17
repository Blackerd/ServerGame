package com.rental.rentalapplication.controller;

import com.rental.rentalapplication.dto.request.RoleRequest;
import com.rental.rentalapplication.dto.response.RoleResponse;
import com.rental.rentalapplication.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    // Tạo vai trò mới
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        RoleResponse roleResponse = roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleResponse);
    }

    // Lấy thông tin vai trò theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
        RoleResponse roleResponse = roleService.getRole(id);
        return ResponseEntity.ok(roleResponse);
    }

    // Xóa vai trò
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Cập nhật vai trò
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable String id, @RequestBody RoleRequest request) {
        RoleResponse roleResponse = roleService.update(id, request);
        return ResponseEntity.ok(roleResponse);
    }

    // Lấy danh sách tất cả vai trò
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAll();
        return ResponseEntity.ok(roles);
    }
}
