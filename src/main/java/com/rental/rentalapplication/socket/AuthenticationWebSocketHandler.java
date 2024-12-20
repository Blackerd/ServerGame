package com.rental.rentalapplication.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.rentalapplication.dto.request.AuthenticationRequest;
import com.rental.rentalapplication.dto.response.AuthenticationResponse;
import com.rental.rentalapplication.dto.response.LeaderBoardResponse;
import com.rental.rentalapplication.dto.response.SystemStatsResponse;
import com.rental.rentalapplication.dto.response.UserResponse;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.exception.Error;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.AuthenticationService;
import com.rental.rentalapplication.service.GameService;
import com.rental.rentalapplication.service.LeaderBoardService;
import com.rental.rentalapplication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationWebSocketHandler extends TextWebSocketHandler {

    private final AuthenticationService authenticationService;
    private final LeaderBoardService leaderBoardService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final GameService gameService;

    // Sử dụng ConcurrentMap để quản lý các WebSocketSession
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: {}", payload);

        try {
            JSONObject jsonObject = new JSONObject(payload);
            String action = jsonObject.getString("action");
            log.info("Action extracted: {}", action);

            // Lấy roles và email từ session attributes
            List<String> roles = (List<String>) session.getAttributes().get("roles");
            String email = (String) session.getAttributes().get("userEmail");
            boolean isAuthenticated = roles != null && !roles.isEmpty();

            log.info("Authenticated user: {} with roles: {}", email, roles);
            log.info("Is Authenticated: {}", isAuthenticated);


            if (!isAuthenticated && !(action.equals("login") || action.equals("register"))) {
                session.sendMessage(new TextMessage("Error: Not authenticated. Please login or register."));
                return;
            }

            // Thiết lập SecurityContext nếu đã xác thực
            if (isAuthenticated) {
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Thêm prefix "ROLE_"
                        .collect(Collectors.toList());
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authenticated user: {} with authorities: {}", email, authorities);
            }

            // Kiểm tra phân quyền cho các hành động admin
            if ((action.equals("blockUser") || action.equals("unblockUser") ||
                    action.equals("activateUser") || action.equals("deactivateUser") ||
                    action.equals("enterMaintenanceMode") || action.equals("exitMaintenanceMode") ||
                    action.equals("reloadServerConfig") || action.equals("getSystemStats") ||
                    action.equals("getAllUsers")) &&
                    (roles == null || !roles.contains("ADMIN"))) {
                session.sendMessage(new TextMessage("Error: You do not have admin rights"));
                return;
            }

            // Switch on the action type
            switch (action) {
                case "login":
                    handleLogin(jsonObject, session);
                    break;
                case "register":
                    handleRegister(jsonObject, session);
                    break;
                case "logout":
                    handleLogout(session);
                    break;
                case "leaderboard":
                    handleLeaderBoard(session);
                    break;
                case "userInfo":
                    handleUserInfo(session);
                    break;
                case "viewRank":
                    handleViewRank(session);
                    break;

                // Các hành động của admin:
                case "blockUser":
                    handleBlockUser(jsonObject, session);
                    break;
                case "unblockUser":
                    handleUnblockUser(jsonObject, session);
                    break;
                case "activateUser":
                    handleActivateUser(jsonObject, session);
                    break;
                case "deactivateUser":
                    handleDeactivateUser(jsonObject, session);
                    break;
                case "enterMaintenanceMode":
                    handleEnterMaintenanceMode(session);
                    break;
                case "exitMaintenanceMode":
                    handleExitMaintenanceMode(session);
                    break;
                case "reloadServerConfig":
                    handleReloadServerConfig(session);
                    break;
                case "getSystemStats":
                    handleGetSystemStats(session);
                    break;
                case "getAllUsers":
                    handleGetAllUsers(jsonObject, session);
                    break;
                case "saveGameResult":
                    handleSaveGameResult(jsonObject, session);
                    log.info("Game result saved successfully", session);
                    break;
                    case "getAllLeaderBoards":
                    handleGetAllLeaderBoards(session);
                    break;

                default:
                    session.sendMessage(new TextMessage("Unknown action"));
                    break;
            }
        } catch (CustomException e) {
            // Handle custom exceptions
            log.error("CustomException: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            log.error("Exception: ", e);
            session.sendMessage(new TextMessage("An unexpected error occurred"));
        } finally {
            // Xóa SecurityContext để tránh rò rỉ thông tin
            SecurityContextHolder.clearContext();
        }
    }

    private void handleLogin(JSONObject jsonObject, WebSocketSession session) throws Exception {
        String email = jsonObject.optString("email");
        String password = jsonObject.optString("password");

        // Kiểm tra dữ liệu đầu vào trước khi gọi service
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            session.sendMessage(new TextMessage("Error: Email and password must not be empty"));
            return;
        }

        AuthenticationRequest loginRequest = new AuthenticationRequest(email, password);
        AuthenticationResponse loginResponse = authenticationService.authenticate(loginRequest);

        if (loginResponse.isAuthenticated()) {
            // Lưu roles và email vào session attributes
            session.getAttributes().put("roles", loginResponse.getRoles());
            session.getAttributes().put("userEmail", loginResponse.getEmail());

            // Cập nhật trạng thái trực tuyến của người dùng thành true
            userService.setUserOnline(loginResponse.getEmail(), true);

        }

        // Gửi phản hồi cho client
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(loginResponse)));
    }

    private void handleRegister(JSONObject jsonObject, WebSocketSession session) throws Exception {
        String email = jsonObject.optString("email");
        String password = jsonObject.optString("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            session.sendMessage(new TextMessage("Error: Email and password must not be empty"));
            return;
        }

        AuthenticationRequest registerRequest = new AuthenticationRequest(email, password);
        AuthenticationResponse registerResponse = authenticationService.register(registerRequest);

        if (registerResponse.isAuthenticated()) {
            // Lưu roles và email vào session attributes
            session.getAttributes().put("roles", registerResponse.getRoles());
            session.getAttributes().put("userEmail", registerResponse.getEmail());
        }

        // Gửi phản hồi cho client
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(registerResponse)));
    }

    private void handleLogout(WebSocketSession session) throws Exception {
        String email = (String) session.getAttributes().get("userEmail");
        if (email != null) {
            // Cập nhật trạng thái trực tuyến của người dùng thành false
            userService.setUserOnline(email, false);
        }
        // Xóa thông tin session
        session.getAttributes().remove("roles");
        session.getAttributes().remove("userEmail");
        session.sendMessage(new TextMessage("Logged out successfully"));
    }

    private void handleLeaderBoard(WebSocketSession session) throws Exception {
        List<LeaderBoardResponse> leaderboard = leaderBoardService.getAllPlayers();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(leaderboard)));
    }

    private void handleUserInfo(WebSocketSession session) throws Exception {
        try {
            UserResponse userInfo = userService.getMyInfo();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(userInfo)));
        } catch (CustomException e) {
            log.error("Error fetching user info: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleViewRank(WebSocketSession session) throws Exception {
        try {
            // Lấy thông tin người dùng hiện tại từ SecurityContext
            var context = SecurityContextHolder.getContext();
            if (context == null || context.getAuthentication() == null) {
                session.sendMessage(new TextMessage("Error: Unauthenticated"));
                return;
            }
            String email = context.getAuthentication().getName();
            UserResponse userResponse = userService.getMyInfo();
            UUID userId = userResponse.getId();

            LeaderBoardResponse userRank = leaderBoardService.getUserRank(userId);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(userRank)));
        } catch (CustomException e) {
            log.error("Error fetching user rank: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    // Các phương thức xử lý cho Admin
    private void handleBlockUser(JSONObject jsonObject, WebSocketSession session) throws Exception {
        String userIdStr = jsonObject.optString("userId");
        if (userIdStr.isBlank()) {
            session.sendMessage(new TextMessage("Error: userId must not be empty"));
            return;
        }
        try {
            UUID userId = UUID.fromString(userIdStr);
            log.info("Blocking user with userId: {}", userId);
            userService.blockUser(userId);
            session.sendMessage(new TextMessage("User blocked successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userIdStr);
            session.sendMessage(new TextMessage("Error: Invalid userId format"));
        } catch (CustomException e) {
            log.error("Error blocking user: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleUnblockUser(JSONObject jsonObject, WebSocketSession session) throws Exception {
        String userIdStr = jsonObject.optString("userId");
        if (userIdStr.isBlank()) {
            session.sendMessage(new TextMessage("Error: userId must not be empty"));
            return;
        }
        try {
            UUID userId = UUID.fromString(userIdStr);
            log.info("Unblocking user with userId: {}", userId);
            userService.unblockUser(userId);
            session.sendMessage(new TextMessage("User unblocked successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userIdStr);
            session.sendMessage(new TextMessage("Error: Invalid userId format"));
        } catch (CustomException e) {
            log.error("Error unblocking user: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleActivateUser(JSONObject jsonObject, WebSocketSession session) throws Exception {
        String userIdStr = jsonObject.optString("userId");
        if (userIdStr.isBlank()) {
            session.sendMessage(new TextMessage("Error: userId must not be empty"));
            return;
        }
        try {
            UUID userId = UUID.fromString(userIdStr);
            log.info("Activating user with userId: {}", userId);
            userService.activateUser(userId);
            session.sendMessage(new TextMessage("User activated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userIdStr);
            session.sendMessage(new TextMessage("Error: Invalid userId format"));
        } catch (CustomException e) {
            log.error("Error activating user: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleDeactivateUser(JSONObject jsonObject, WebSocketSession session) throws Exception {
        String userIdStr = jsonObject.optString("userId");
        if (userIdStr.isBlank()) {
            session.sendMessage(new TextMessage("Error: userId must not be empty"));
            return;
        }
        try {
            UUID userId = UUID.fromString(userIdStr);
            log.info("Deactivating user with userId: {}", userId);
            userService.deactivateUser(userId);
            session.sendMessage(new TextMessage("User deactivated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userIdStr);
            session.sendMessage(new TextMessage("Error: Invalid userId format"));
        } catch (CustomException e) {
            log.error("Error deactivating user: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleEnterMaintenanceMode(WebSocketSession session) throws Exception {
        userService.enterMaintenanceMode();
        session.sendMessage(new TextMessage("Maintenance mode enabled"));
    }

    private void handleExitMaintenanceMode(WebSocketSession session) throws Exception {
        userService.exitMaintenanceMode();
        session.sendMessage(new TextMessage("Maintenance mode disabled"));
    }

    private void handleReloadServerConfig(WebSocketSession session) throws Exception {
        userService.reloadServerConfig();
        session.sendMessage(new TextMessage("Server config reloaded"));
    }

    private void handleGetSystemStats(WebSocketSession session) throws Exception {
        try {
            SystemStatsResponse stats = userService.getSystemStats();
            String jsonResponse = objectMapper.writeValueAsString(stats);
            session.sendMessage(new TextMessage(jsonResponse));
        } catch (CustomException e) {
            log.error("Error fetching system stats: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    private void handleGetAllUsers(JSONObject jsonObject, WebSocketSession session) throws Exception {
        Integer pageNo = jsonObject.optInt("pageNo", 1);
        Integer pageSize = jsonObject.optInt("pageSize", 10);

        log.info("Fetching users: pageNo={}, pageSize={}", pageNo, pageSize);

        if (pageNo <= 0 || pageSize <= 0) {
            session.sendMessage(new TextMessage("Error: pageNo and pageSize must be positive integers"));
            return;
        }

        try {
            org.springframework.data.domain.Page<com.rental.rentalapplication.dto.response.UserResponse> usersPage = userService.getAllUsers(pageNo, pageSize);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(usersPage)));
        } catch (Exception e) {
            log.error("Error fetching users", e);
            session.sendMessage(new TextMessage("Error: Unable to fetch users"));
        }
    }
    private void handleSaveGameResult(JSONObject jsonObject, WebSocketSession session) throws Exception {
        int score = jsonObject.optInt("score", -1);
        long duration = jsonObject.optLong("duration", -1);

        if (score < 0 || duration < 0) {
            session.sendMessage(new TextMessage("Error: Invalid 'score' or 'duration' value."));
            return;
        }

        String email = (String) session.getAttributes().get("userEmail");
        if (email == null) {
            session.sendMessage(new TextMessage("Error: User not authenticated."));
            return;
        }

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(Error.USER_NOT_EXISTED));

            gameService.saveGameSession(user, score, duration);

            List<LeaderBoardResponse> updatedLeaderBoard = leaderBoardService.getAllPlayers();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(updatedLeaderBoard)));
        } catch (CustomException e) {
            log.error("Error saving game result: {}", e.getMessage());
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }


    private void broadcastLeaderBoard(List<LeaderBoardResponse> leaderBoards) {
        String message;
        try {
            message = objectMapper.writeValueAsString(leaderBoards);
        } catch (Exception e) {
            log.error("Error serializing LeaderBoard: ", e);
            return;
        }

        sessions.forEach((email, session) -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("Error sending message to user {}: {}", email, e.getMessage());
            }
        });

        log.info("Broadcasted LeaderBoard to {} users", sessions.size());
    }

    private void handleGetAllLeaderBoards(WebSocketSession session) throws Exception {
        List<LeaderBoardResponse> leaderBoards = leaderBoardService.getAllLeaderBoards();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(leaderBoards)));
    }
}
