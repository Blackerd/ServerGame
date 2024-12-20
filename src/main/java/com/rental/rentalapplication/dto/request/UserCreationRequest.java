package com.rental.rentalapplication.dto.request;


import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "MAIL_INVALID")
    String email;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    LocalDate dob;
}