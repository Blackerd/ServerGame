package com.rental.rentalapplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OnlineUserDTO {
    private UUID id;
    private String email;
    private Set<String> roles;
}
