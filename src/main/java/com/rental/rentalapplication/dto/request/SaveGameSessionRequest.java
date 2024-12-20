package com.rental.rentalapplication.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SaveGameSessionRequest {
    private UUID userIds; // Danh sách ID người chơi
    private int score; // Tổng điểm
    private long duration; // Thời gian chơi
}
