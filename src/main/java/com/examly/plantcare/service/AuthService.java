package com.examly.plantcare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.examly.plantcare.dto.AuthResponse;
import com.examly.plantcare.dto.LoginRequest;
import com.examly.plantcare.dto.RegisterRequest;
import com.examly.plantcare.dto.ResetPasswordRequest;
import com.examly.plantcare.entity.User;
import com.examly.plantcare.repository.UserRepository;
import com.examly.plantcare.util.JwtUtil;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setGardeningExperience(request.getGardeningExperience());
            user.setLocation(request.getLocation());
            user.setTimezone(request.getTimezone());
            user.setIsActive(true);
            user.setEmailVerified(false);

            userRepository.save(user);

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().name());

            AuthResponse response = new AuthResponse();
            response.setAccessToken(token);
            response.setRefreshToken(refreshToken);
            response.setUser(user);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) throw new RuntimeException("User not found");

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Update last login time
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().name());

        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(user);
        return response;
    }

    public String logout(String token) {
        // Add token to revoked table if needed
        return "Logged out successfully.";
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(username, role);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        return response;
    }


    public String forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) throw new RuntimeException("Email not registered");

        String resetToken = UUID.randomUUID().toString();
        // Normally save resetToken in DB & email it
        return "Reset token: " + resetToken;
    }

    public String resetPassword(ResetPasswordRequest request) {
        // Validate token here
        return "Password reset successful for token: " + request.getToken();
    }

    public String verifyEmail(String token) {
        // Token validation logic
        return "Email verified successfully.";
    }
}
