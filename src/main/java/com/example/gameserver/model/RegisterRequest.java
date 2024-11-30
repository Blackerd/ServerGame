package com.example.gameserver.model;

public class RegisterRequest {

    private String username;  // Tên đăng nhập
    private String email;     // Email
    private String password;  // Mật khẩu người dùng

    // Constructor không tham số (mặc định)
    public RegisterRequest() {}

    // Constructor với tham số
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
