package com.examly.plantcare.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    // Manual getter to fix Lombok issues
    public String getToken() {
        return token;
    }
}
