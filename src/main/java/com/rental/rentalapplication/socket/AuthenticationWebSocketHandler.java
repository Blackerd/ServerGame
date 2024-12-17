package com.rental.rentalapplication.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.rentalapplication.dto.request.AuthenticationRequest;
import com.rental.rentalapplication.dto.request.LogoutRequest;
import com.rental.rentalapplication.dto.request.SaveGameSessionRequest;
import com.rental.rentalapplication.dto.response.AuthenticationResponse;
import com.rental.rentalapplication.dto.response.UserResponse;
import com.rental.rentalapplication.entity.LeaderBoard;
import com.rental.rentalapplication.entity.User;
import com.rental.rentalapplication.exception.CustomException;
import com.rental.rentalapplication.repository.UserRepository;
import com.rental.rentalapplication.service.AuthenticationService;
import com.rental.rentalapplication.service.GameService;
import com.rental.rentalapplication.service.LeaderBoardService;
import com.rental.rentalapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.UUID;

@Component
public class AuthenticationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LeaderBoardService leaderBoardService;

    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper; // For parsing JSON

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        try {
            // Extract the action from the payload (assuming JSON structure has an "action" field)
            String action = extractAction(payload);

            // Switch on the action type
            switch (action) {
                case "login":
                    AuthenticationRequest loginRequest = objectMapper.readValue(payload, AuthenticationRequest.class);
                    AuthenticationResponse loginResponse = authenticationService.authenticate(loginRequest);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(loginResponse)));
                    break;

                case "register":
                    AuthenticationRequest registerRequest = objectMapper.readValue(payload, AuthenticationRequest.class);
                    AuthenticationResponse registerResponse = authenticationService.register(registerRequest);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(registerResponse)));
                    break;

                case "logout":
                    LogoutRequest logoutRequest = objectMapper.readValue(payload, LogoutRequest.class);
                    authenticationService.logout(logoutRequest);
                    session.sendMessage(new TextMessage("Logged out successfully"));
                    break;
                case "leaderboard":
                    List<LeaderBoard> leaderboard = leaderBoardService.getAllPlayers();
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(leaderboard)));
                    break;
                case "userInfo":
                    // Assuming you have some way of identifying the logged-in user from the session.
                    UserResponse loggedInUser = userService.getMyInfo(); // Or some method to identify logged-in user
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(loggedInUser)));
                    break;
                case "gameSession":
                    SaveGameSessionRequest gameSessionRequest = objectMapper.readValue(payload, SaveGameSessionRequest.class);
                    // Ensure gameSessionRequest contains a list of UUIDs for users, score, and duration
                    List<UUID> userIds = gameSessionRequest.getUserIds();
                    List<User> users = userRepository.findAllById(userIds.stream().map(UUID::toString).toList());
                    if (users.isEmpty()) {
                        session.sendMessage(new TextMessage("Invalid user IDs"));
                    } else {
                        gameService.saveGameSession(users, gameSessionRequest.getScore(), gameSessionRequest.getDuration());
                        session.sendMessage(new TextMessage("Game session saved successfully"));
                    }
                    break;


                default:
                    // Handle unknown actions
                    session.sendMessage(new TextMessage("Unknown action"));
                    break;
            }
        } catch (CustomException e) {
            // Handle custom exceptions
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            session.sendMessage(new TextMessage("An unexpected error occurred"));
        }
    }

    // Utility method to extract the action from the payload
    private String extractAction(String payload) {
        try {
            // Parse the payload and extract the "action" field
            return objectMapper.readTree(payload).path("action").asText();
        } catch (Exception e) {
            return "";
        }
    }
}
