package com.rental.rentalapplication.controller;

import com.nimbusds.jose.JOSEException;

import com.rental.rentalapplication.dto.request.AuthenticationRequest;
import com.rental.rentalapplication.dto.request.IntrospectRequest;
import com.rental.rentalapplication.dto.request.LogoutRequest;
import com.rental.rentalapplication.dto.request.RefreshRequest;
import com.rental.rentalapplication.dto.response.ApiResponse;
import com.rental.rentalapplication.dto.response.AuthenticationResponse;
import com.rental.rentalapplication.dto.response.IntrospectResponse;
import com.rental.rentalapplication.service.impl.AuthenticationServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    AuthenticationServiceImpl authenticationServiceImpl;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationServiceImpl.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Authentication successful")
                .status("success")
                .timeStamp(LocalDateTime.now())
                .responseData(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> intropspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationServiceImpl.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .message("Introspection successful")
                .status("success")
                .timeStamp(LocalDateTime.now())
                .responseData(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationServiceImpl.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Refresh successful")
                .status("success")
                .timeStamp(LocalDateTime.now())
                .responseData(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationServiceImpl.logout(request);
        return ApiResponse.<Void>builder().status("Success").build();
    }


}
