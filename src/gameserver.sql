CREATE DATABASE GameServerDB;
USE GameServerDB;

-- Bảng lưu thông tin người chơi
CREATE TABLE players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,  -- Tên đăng nhập
    email VARCHAR(100) NOT NULL UNIQUE,    -- Email đăng ký
    password_hash VARCHAR(255) NOT NULL,   -- Mật khẩu mã hóa
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng lưu thông tin các phiên chơi
CREATE TABLE game_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,             -- Liên kết đến người chơi
    score INT NOT NULL,                    -- Điểm số trong phiên chơi
    duration BIGINT NOT NULL,              -- Thời gian chơi (giây)
    session_time DATETIME NOT NULL,        -- Thời điểm chơi
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Thêm chỉ số (index) cho game_sessions
CREATE INDEX idx_game_sessions_player_id ON game_sessions(player_id);

-- Bảng lưu thông tin bảng xếp hạng
CREATE TABLE leaderboard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,             -- Liên kết đến người chơi
    event_name VARCHAR(100) NOT NULL,      -- Tên sự kiện hoặc mùa giải
    score INT NOT NULL,                    -- Điểm số
    rank INT,                              -- Xếp hạng
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    UNIQUE(player_id, event_name)          -- Ràng buộc duy nhất
);

-- Thêm chỉ số (index) cho leaderboard
CREATE INDEX idx_leaderboard_player_event ON leaderboard(player_id, event_name);
