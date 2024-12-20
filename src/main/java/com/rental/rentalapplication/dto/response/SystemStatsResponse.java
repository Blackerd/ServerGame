package com.rental.rentalapplication.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SystemStatsResponse {
    private long onlineUsers;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime serverTime;

    private boolean maintenanceMode;
    private double cpuUsage;
    private double memoryUsage;

    private List<OnlineUserDTO> onlineUserDetails; // Danh sách chi tiết người dùng trực tuyến
}
