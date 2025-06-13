package com.teamlogger.backend.dto;

import com.teamlogger.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private User.UserRole role;
    private boolean isActive;
    private boolean isOnline;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String profileImageUrl;
    private String timezone;
    private String language;
    private Integer workHoursPerDay;
    
    // For password change
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String confirmPassword;
    
    // For password reset
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
} 