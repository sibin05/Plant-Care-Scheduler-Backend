package com.examly.plantcare.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Boolean isActive = true;
    private Boolean emailVerified = false;
    private String location;

    @Enumerated(EnumType.STRING)
    private GardeningExperience gardeningExperience;

    private String timezone;

    @Column(columnDefinition = "json")
    private String notificationPreferences; // store JSON as String

    private LocalDateTime createdDateTime = LocalDateTime.now();
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "user")
    private List<Plant> plants = new ArrayList<>();

    @OneToMany(mappedBy = "completedBy")
    @JsonManagedReference(value = "users")
    private List<CareTask> completedTasks;


    public enum Role {
        ADMIN,
        PLANT_CARE_SPECIALIST,
        PREMIUM_PLANT_OWNER,
        STANDARD_PLANT_OWNER,
        COMMUNITY_MEMBER,
        GUEST
    }

    public enum GardeningExperience {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }

    // Manual getters and setters to fix Lombok issues
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setGardeningExperience(GardeningExperience gardeningExperience) {
        this.gardeningExperience = gardeningExperience;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setLastLogin(java.time.LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public java.time.LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public GardeningExperience getGardeningExperience() {
        return gardeningExperience;
    }

    // Alias for frontend compatibility
    public String getGardeningExp() {
        return gardeningExperience != null ? gardeningExperience.toString() : null;
    }

    public void setGardeningExp(String gardeningExp) {
        if (gardeningExp != null && !gardeningExp.isEmpty()) {
            try {
                this.gardeningExperience = GardeningExperience.valueOf(gardeningExp.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid enum value gracefully
                this.gardeningExperience = null;
            }
        }
    }
}
