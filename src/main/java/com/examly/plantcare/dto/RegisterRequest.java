package com.examly.plantcare.dto;

import com.examly.plantcare.entity.User.GardeningExperience;
import com.examly.plantcare.entity.User.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password; // raw password, will be hashed before saving
    private Role role;
    private GardeningExperience gardeningExperience;
    private String location;
    private String timezone;
    private Boolean isActive = true;
    private Boolean emailVerified = false;
    private String notificationPreferences; // JSON string like {"email":true,"sms":false}

    // Manual getters to fix Lombok issues
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public GardeningExperience getGardeningExperience() {
        return gardeningExperience;
    }

    public String getLocation() {
        return location;
    }

    public String getTimezone() {
        return timezone;
    }
}
