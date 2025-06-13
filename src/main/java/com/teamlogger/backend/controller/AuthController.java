package com.teamlogger.backend.controller;

import com.teamlogger.backend.dto.LoginRequest;
import com.teamlogger.backend.dto.UserDto;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.security.JwtTokenProvider;
import com.teamlogger.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        userService.updateOnlineStatus(user.getId(), true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("tokenType", "Bearer");
        response.put("user", convertToDto(user));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(userDto);
        return ResponseEntity.ok(convertToDto(user));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String username = tokenProvider.getUsernameFromToken(token.substring(7));
        User user = (User) userService.loadUserByUsername(username);
        userService.updateOnlineStatus(user.getId(), false);
        
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.initiatePasswordReset(email);
            return ResponseEntity.ok().body(Map.of("message", "Password reset email sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String resetToken, @RequestParam String newPassword) {
        try {
            userService.resetPassword(resetToken, newPassword);
            return ResponseEntity.ok().body(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String token,
                                          @RequestParam String currentPassword,
                                          @RequestParam String newPassword) {
        try {
            String username = tokenProvider.getUsernameFromToken(token.substring(7));
            User user = (User) userService.loadUserByUsername(username);
            userService.changePassword(user.getId(), currentPassword, newPassword);
            return ResponseEntity.ok().body(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = tokenProvider.getUsernameFromToken(token.substring(7));
            User user = (User) userService.loadUserByUsername(username);
            return ResponseEntity.ok(convertToDto(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setIsActive(user.isActive());
        dto.setIsOnline(user.isOnline());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setTimezone(user.getTimezone());
        dto.setLanguage(user.getLanguage());
        dto.setWorkHoursPerDay(user.getWorkHoursPerDay());
        return dto;
    }
} 