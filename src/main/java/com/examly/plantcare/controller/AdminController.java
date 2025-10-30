package com.examly.plantcare.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.examly.plantcare.entity.User;
import com.examly.plantcare.repository.UserRepository;
import com.examly.plantcare.repository.PlantRepository;
import com.examly.plantcare.repository.CareTaskRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PlantRepository plantRepository;
    
    @Autowired
    private CareTaskRepository careTaskRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            System.out.println("[ADMIN] Fetching all users");
            List<User> users = userRepository.findAll();
            System.out.println("[ADMIN] Found " + users.size() + " users");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("[ADMIN] Error fetching users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            System.out.println("[ADMIN] Creating user: " + user.getUsername());
            
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                System.out.println("[ADMIN] Username already exists: " + user.getUsername());
                return ResponseEntity.badRequest().body("{\"message\":\"Username already exists\"}");
            }
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                System.out.println("[ADMIN] Email already exists: " + user.getEmail());
                return ResponseEntity.badRequest().body("{\"message\":\"Email already exists\"}");
            }
            
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            user.setIsActive(true);
            user.setEmailVerified(true);
            
            User savedUser = userRepository.save(user);
            System.out.println("[ADMIN] User created successfully: " + savedUser.getId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            System.err.println("[ADMIN] Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Failed to create user: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (updates.containsKey("role")) {
                user.setRole(User.Role.valueOf((String) updates.get("role")));
            }
            if (updates.containsKey("isActive")) {
                user.setIsActive((Boolean) updates.get("isActive"));
            }
            if (updates.containsKey("email")) {
                user.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("location")) {
                user.setLocation((String) updates.get("location"));
            }
            if (updates.containsKey("gardeningExperience")) {
                user.setGardeningExperience(User.GardeningExperience.valueOf((String) updates.get("gardeningExperience")));
            }
            
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Failed to update user: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok("{\"message\":\"User deleted successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Failed to delete user: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Total users
            long totalUsers = userRepository.count();
            stats.put("totalUsers", totalUsers);
            
            // Users by role
            Map<String, Long> usersByRole = new HashMap<>();
            for (User.Role role : User.Role.values()) {
                long count = userRepository.countByRole(role);
                usersByRole.put(role.name(), count);
            }
            stats.put("usersByRole", usersByRole);
            
            // Total plants
            long totalPlants = plantRepository.count();
            stats.put("totalPlants", totalPlants);
            
            // Active tasks
            long activeTasks = careTaskRepository.countByStatus(com.examly.plantcare.entity.CareTask.Status.PENDING);
            stats.put("activeTasks", activeTasks);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}