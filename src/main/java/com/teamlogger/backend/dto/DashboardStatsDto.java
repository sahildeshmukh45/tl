package com.teamlogger.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    
    // User statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long onlineUsers;
    
    // Time tracking statistics
    private Double totalHoursToday;
    private Double totalHoursThisWeek;
    private Double totalHoursThisMonth;
    private Double averageHoursPerDay;
    private Double overtimeHours;
    
    // Project statistics
    private Long totalProjects;
    private Long activeProjects;
    private Long completedProjects;
    private Double averageProjectProgress;
    
    // Task statistics
    private Long totalTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long overdueTasks;
    private Double taskCompletionRate;
    
    // Performance metrics
    private Map<String, Double> productivityByUser;
    private Map<String, Double> hoursByProject;
    private List<ChartDataPoint> weeklyHours;
    private List<ChartDataPoint> monthlyHours;
    
    // Recent activities
    private List<RecentActivityDto> recentActivities;
    
    // Alerts and notifications
    private Long unreadNotifications;
    private List<String> alerts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataPoint {
        private String label;
        private Double value;
        private String date;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDto {
        private String type;
        private String description;
        private String userName;
        private String timestamp;
        private String actionUrl;
    }
} 