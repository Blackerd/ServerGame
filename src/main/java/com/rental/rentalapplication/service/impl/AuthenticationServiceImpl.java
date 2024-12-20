package com.rental.rentalapplication.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.rental.rentalapplication.dto.request.AuthenticationRequest;
import com.rental.rentalapplication.dto.request.IntrospectRequest;
import com.rental.rentalapplication.dto.request.LogoutRequest;
import com.rental.rentalapplication.dto.request.RefreshRequest;
import com.rental.rentalapplication.dto.response.AuthenticationResponse;
import com.rental.rentalapplication.dto.response.IntrospectResponse;
import com.rental.rentalapplication.entity.InvalidatedToken;
import com.rental.rentalapplication.entity.Role;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.exception.Error;
import com.rental.rentalapplication.repository.InvalidatedTokenRepository;
import com.rental.rentalapplication.repository.RoleRepository;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.AuthenticationService;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthenticationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var player = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new CustomException(Error.USER_NOT_EXISTED)
        );
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), player.getPassword());

        if (!authenticated)
            throw new CustomException(Error.UNAUTHENTICATED);

        var token = generateToken(player);

        List<String> roleNames = new ArrayList<>();
        for (var role : player.getRoles()) {
            roleNames.add(role.getName());
        }

        return AuthenticationResponse.builder()
                .token(token)
                .email(player.getEmail())
                .roles(roleNames)
                .isAuthenticated(true)
                .build();

    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (CustomException exception) {
            log.info("Token already expired");
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (CustomException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByEmail(username).orElseThrow(() -> new CustomException(Error.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).isAuthenticated(true).build();
    }

    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new CustomException(Error.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new CustomException(Error.UNAUTHENTICATED);

        return signedJWT;
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        }
        return stringJoiner.toString();
    }

    public AuthenticationResponse register(AuthenticationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(Error.USER_EXISTED); // Người dùng đã tồn tại
        }
        if (request.getPassword().length() < 8) {
            throw new CustomException(Error.PASSWORD_INVALID);
        }

        // Mã hóa mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Tạo người dùng mới
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        // Gán role USER mặc định
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findById("USER").orElseThrow(() -> new CustomException(Error.ROLE_NOT_FOUND));
        roles.add(role);

        user.setRoles(roles);
        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);

        // Tạo token cho người dùng mới
        var token = generateToken(user);

        // Tạo danh sách roleNames (nếu cần trả về trong response)
        List<String> roleNames = new ArrayList<>();
        for (var roleItem : user.getRoles()) {
            roleNames.add(roleItem.getName());
        }

        // Trả về thông tin người dùng mới cùng token
        return AuthenticationResponse.builder()
                .token(token)
                .email(user.getEmail())
                .roles(roleNames)
                .isAuthenticated(true)
                .build();
    }



}
