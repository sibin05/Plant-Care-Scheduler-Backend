package com.examly.plantcare.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    // Manual getters to fix Lombok issues
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
