package com.example.gameserver.socket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendScoreUpdate(String email, int score) {
        simpMessagingTemplate.convertAndSend("/topic/score." + email, score);
    }

    @GetMapping("/game")
    public String getGamePage() {
        return "game.html";  // Trả về giao diện HTML cho game
    }

}
