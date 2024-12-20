package com.rental.rentalapplication.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;     // Email
    private String password;  // Mật khẩu người dùng

}