package com.rental.rentalapplication.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.rental.rentalapplication.dto.request.AuthenticationRequest;
import com.rental.rentalapplication.dto.request.IntrospectRequest;
import com.rental.rentalapplication.dto.request.LogoutRequest;
import com.rental.rentalapplication.dto.request.RefreshRequest;
import com.rental.rentalapplication.dto.response.AuthenticationResponse;
import com.rental.rentalapplication.dto.response.IntrospectResponse;
import com.rental.rentalapplication.entity.User;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;

    SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException;

    String generateToken(User user);
}
