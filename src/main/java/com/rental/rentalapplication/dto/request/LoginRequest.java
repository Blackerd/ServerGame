package com.rental.rentalapplication.dto.request;


public class LoginRequest {
    private String username;
    private String password;

    // Getter và Setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}