package com.teamlogger.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "time_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    
    @Column(name = "punch_in_time", nullable = false)
    private LocalDateTime punchInTime;
    
    @Column(name = "punch_out_time")
    private LocalDateTime punchOutTime;
    
    @Column(name = "lunch_in_time")
    private LocalDateTime lunchInTime;
    
    @Column(name = "lunch_out_time")
    private LocalDateTime lunchOutTime;
    
    @Column(name = "break_start_time")
    private LocalDateTime breakStartTime;
    
    @Column(name = "break_end_time")
    private LocalDateTime breakEndTime;
    
    @Column(name = "total_work_hours")
    private Double totalWorkHours = 0.0;
    
    @Column(name = "total_break_hours")
    private Double totalBreakHours = 0.0;
    
    @Column(name = "total_lunch_hours")
    private Double totalLunchHours = 0.0;
    
    @Column(name = "overtime_hours")
    private Double overtimeHours = 0.0;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_manual_entry")
    private boolean isManualEntry = false;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "is_approved")
    private boolean isApproved = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void calculateHours() {
        if (punchInTime != null && punchOutTime != null) {
            Duration totalDuration = Duration.between(punchInTime, punchOutTime);
            totalWorkHours = totalDuration.toMinutes() / 60.0;
            
            // Calculate break hours
            if (breakStartTime != null && breakEndTime != null) {
                Duration breakDuration = Duration.between(breakStartTime, breakEndTime);
                totalBreakHours = breakDuration.toMinutes() / 60.0;
            }
            
            // Calculate lunch hours
            if (lunchInTime != null && lunchOutTime != null) {
                Duration lunchDuration = Duration.between(lunchInTime, lunchOutTime);
                totalLunchHours = lunchDuration.toMinutes() / 60.0;
            }
            
            // Calculate overtime (assuming 8 hours work day)
            double regularHours = 8.0;
            double actualWorkHours = totalWorkHours - totalBreakHours - totalLunchHours;
            overtimeHours = Math.max(0, actualWorkHours - regularHours);
        }
    }
    
    public boolean isCurrentlyActive() {
        return isActive && punchInTime != null && punchOutTime == null;
    }
    
    public boolean isOnBreak() {
        return breakStartTime != null && breakEndTime == null;
    }
    
    public boolean isOnLunch() {
        return lunchInTime != null && lunchOutTime == null;
    }
} 