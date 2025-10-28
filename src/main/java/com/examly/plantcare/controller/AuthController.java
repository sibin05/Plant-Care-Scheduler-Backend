package com.examly.plantcare.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.examly.plantcare.dto.LoginRequest;
import com.examly.plantcare.dto.RegisterRequest;
import com.examly.plantcare.entity.PasswordResetToken;
import com.examly.plantcare.entity.User;
import com.examly.plantcare.repository.PasswordResetTokenRepository;
import com.examly.plantcare.repository.UserRepository;
import com.examly.plantcare.service.AuthService;
import com.examly.plantcare.service.EmailService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("Registration request received for user: " + request.getUsername());
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            System.err.println("Registration error: " + e.getMessage());
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\":\"" + e.getMessage() + "\"}");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Registration failed: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Registration failed: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.of(userRepository.findByUsername(username));
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User updatedUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getLocation() != null) user.setLocation(updatedUser.getLocation());
        if (updatedUser.getGardeningExp() != null) user.setGardeningExp(updatedUser.getGardeningExp());
        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.logout(token));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping("/verify-email/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth service is working!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        System.out.println("[FORGOT-PASSWORD] Request received with payload: " + payload);
        
        String email = payload.get("email");
        System.out.println("[FORGOT-PASSWORD] Processing email: " + email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            System.out.println("[FORGOT-PASSWORD] Email not found in database: " + email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\":\"Email not registered\"}");
        }
        User user = userOpt.get();
        System.out.println("[FORGOT-PASSWORD] User found: " + user.getUsername() + " (ID: " + user.getId() + ")");

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        System.out.println("[FORGOT-PASSWORD] Generated OTP: " + otp);
        
        PasswordResetToken token = tokenRepository.findByUser(user);
        if (token == null) {
            System.out.println("[FORGOT-PASSWORD] Creating new password reset token");
            token = new PasswordResetToken();
        } else {
            System.out.println("[FORGOT-PASSWORD] Updating existing password reset token");
        }
        
        token.setUser(user);
        token.setOtp(otp);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(10));
        
        try {
            tokenRepository.save(token);
            System.out.println("[FORGOT-PASSWORD] Token saved successfully");
        } catch (Exception e) {
            System.err.println("[FORGOT-PASSWORD] Error saving token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Failed to generate OTP\"}");
        }

        String htmlContent = "<div style='font-family:Arial,sans-serif;padding:20px;border:1px solid #eee;border-radius:10px;max-width:400px;margin:auto'>" +
                "<h2 style='color:#4CAF50;'>Password Reset OTP</h2>" +
                "<p>Use the following OTP to reset your password:</p>" +
                "<p style='font-size:24px;font-weight:bold;color:#FF5722;'>" + otp + "</p>" +
                "<p>This OTP is valid for 10 minutes.</p>" +
                "<p>If you didn't request this, please ignore this email.</p></div>";

        try {
            System.out.println("[FORGOT-PASSWORD] Attempting to send email to: " + user.getEmail());
            emailService.sendHtmlMessage(user.getEmail(), "Your OTP for Password Reset", htmlContent);
            System.out.println("[FORGOT-PASSWORD] Email sent successfully");
            return ResponseEntity.ok("{\"message\":\"OTP sent to your registered email\"}");
        } catch (Exception e) {
            System.err.println("[FORGOT-PASSWORD] Failed to send email: " + e.getMessage());
            System.out.println("=== PASSWORD RESET OTP FOR " + user.getEmail() + " ===");
            System.out.println("OTP: " + otp);
            System.out.println("Valid until: " + token.getExpirationTime());
            System.out.println("===============================================");
            return ResponseEntity.ok("{\"message\":\"OTP generated (check console - email failed)\"}");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        System.out.println("[RESET-PASSWORD] Request received");
        String email = payload.get("email");
        String otp = payload.get("otp");
        String newPassword = payload.get("newPassword");
        System.out.println("[RESET-PASSWORD] Email: " + email + ", OTP: " + otp);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found\"}");
        }
        User user = userOpt.get();

        PasswordResetToken token = tokenRepository.findByUser(user);
        if (token == null) {
            System.out.println("[RESET-PASSWORD] No token found for user");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Invalid OTP\"}");
        }
        
        System.out.println("[RESET-PASSWORD] Token OTP: " + token.getOtp() + ", Received OTP: " + otp);
        if (!token.getOtp().equals(otp)) {
            System.out.println("[RESET-PASSWORD] OTP mismatch");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Invalid OTP\"}");
        }

        if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"OTP expired\"}");
        }

        System.out.println("[RESET-PASSWORD] Updating password for user: " + user.getUsername());
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token);
        System.out.println("[RESET-PASSWORD] Password reset completed successfully");

        return ResponseEntity.ok("{\"message\":\"Password reset successful\"}");
    }
}