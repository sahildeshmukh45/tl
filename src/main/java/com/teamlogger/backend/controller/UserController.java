package com.teamlogger.backend.controller;

import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.repository.UserRepository;
import com.teamlogger.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);
        user.setIsOnline(false);
        
        User savedUser = userRepository.save(user);
        
        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(), user.getUsername());
        
        return ResponseEntity.ok(savedUser);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            user.setIsActive(userDetails.isIsActive());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Only update password if provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<User>> getActiveUsers() {
        List<User> users = userRepository.findByIsActive(true);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/online")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<User>> getOnlineUsers() {
        List<User> users = userRepository.findByIsOnline(true);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable User.UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            user.setUpdatedAt(LocalDateTime.now());
            User activatedUser = userRepository.save(user);
            return ResponseEntity.ok(activatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            user.setIsOnline(false);
            user.setUpdatedAt(LocalDateTime.now());
            User deactivatedUser = userRepository.save(user);
            return ResponseEntity.ok(deactivatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> resetUserPassword(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String newPassword = generateRandomPassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            
            // Send password reset email
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), newPassword);
            
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getUserStats() {
        return ResponseEntity.ok(Map.of(
                "totalUsers", userRepository.count(),
                "activeUsers", userRepository.countByIsActive(true),
                "onlineUsers", userRepository.countOnlineUsers(),
                "adminUsers", userRepository.countByRole(User.UserRole.ADMIN),
                "managerUsers", userRepository.countByRole(User.UserRole.MANAGER),
                "regularUsers", userRepository.countByRole(User.UserRole.USER)
        ));
    }
    
    private String generateRandomPassword() {
        // Simple password generation - in production, use a more secure method
        return "TempPass" + System.currentTimeMillis() % 1000;
    }
} 