package com.teamlogger.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryDto {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private Long projectId;
    private Long taskId;
    
    @NotNull(message = "Punch in time is required")
    private LocalDateTime punchInTime;
    
    private LocalDateTime punchOutTime;
    private LocalDateTime lunchInTime;
    private LocalDateTime lunchOutTime;
    private LocalDateTime breakStartTime;
    private LocalDateTime breakEndTime;
    
    private Double totalWorkHours;
    private Double totalBreakHours;
    private Double totalLunchHours;
    private Double overtimeHours;
    
    private boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isManualEntry;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private boolean isApproved;
    
    // Additional fields for UI
    private String userName;
    private String projectName;
    private String taskTitle;
    private String status;
} 