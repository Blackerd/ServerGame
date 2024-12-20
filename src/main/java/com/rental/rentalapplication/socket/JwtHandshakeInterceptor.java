package com.rental.rentalapplication.socket;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServletServerHttpRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        // Nếu client gửi token trong query param khi kết nối WebSocket
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // Lấy token từ query param
            String token = httpRequest.getParameter("token");
            if (token != null && !token.isEmpty()) {
                try {
                    // Giải mã token
                    String userEmail = getEmailFromToken(token);
                    List<String> roles = getRolesFromToken(token);

                    // Lưu thông tin vào attributes của session
                    attributes.put("userEmail", userEmail);
                    attributes.put("roles", roles);
                } catch (ParseException e) {
                    // Token không hợp lệ, có thể từ chối kết nối hoặc chỉ đơn giản không lưu thông tin
                    return false;
                }
            }
        }

        // Tiếp tục handshake bất kể có token hay không
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Không cần xử lý gì sau handshake
    }

    // Hàm giải mã token để lấy email
    private String getEmailFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        return claims.getSubject();
    }

    // Hàm giải mã token để lấy roles
    private List<String> getRolesFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // Giả sử roles được lưu trong claim "scope" dưới dạng chuỗi "ROLE_USER ROLE_ADMIN"
        String scope = (String) claims.getClaim("scope");

        if (scope == null || scope.isBlank()) {
            return List.of("USER");
        }

        return Arrays.stream(scope.split(" "))
                .filter(s -> !s.isBlank())
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }
}
