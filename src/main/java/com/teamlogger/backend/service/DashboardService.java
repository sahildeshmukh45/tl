package com.teamlogger.backend.service;

import com.teamlogger.backend.dto.DashboardStatsDto;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ScreenshotRepository screenshotRepository;
    
    public DashboardStatsDto getDashboardStats(Long userId) {
        DashboardStatsDto stats = new DashboardStatsDto();
        
        // User statistics
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByIsActive(true));
        stats.setOnlineUsers(userRepository.countOnlineUsers());
        
        // Time tracking statistics for current week
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        LocalDate endOfWeek = LocalDate.now();
        
        Double totalHoursThisWeek = timeEntryRepository.calculateTotalHoursForUserInDateRange(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.setTotalHoursThisWeek(totalHoursThisWeek != null ? totalHoursThisWeek : 0.0);
        
        Double overtimeHours = timeEntryRepository.calculateTotalOvertimeForUserInDateRange(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.setOvertimeHours(overtimeHours != null ? overtimeHours : 0.0);
        
        // Project statistics
        stats.setTotalProjects(projectRepository.count());
        stats.setActiveProjects(projectRepository.countByStatus(com.teamlogger.backend.entity.Project.ProjectStatus.ACTIVE));
        stats.setCompletedProjects(projectRepository.countByStatus(com.teamlogger.backend.entity.Project.ProjectStatus.COMPLETED));
        
        Double avgProgress = projectRepository.calculateAverageProjectProgress();
        stats.setAverageProjectProgress(avgProgress != null ? avgProgress : 0.0);
        
        // Task statistics
        stats.setTotalTasks(taskRepository.count());
        stats.setCompletedTasks(taskRepository.countByStatus(com.teamlogger.backend.entity.Task.TaskStatus.COMPLETED));
        stats.setInProgressTasks(taskRepository.countByStatus(com.teamlogger.backend.entity.Task.TaskStatus.IN_PROGRESS));
        stats.setOverdueTasks(taskRepository.findOverdueTasks(LocalDateTime.now()).size());
        
        // Calculate task completion rate
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus(com.teamlogger.backend.entity.Task.TaskStatus.COMPLETED);
        stats.setTaskCompletionRate(totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0.0);
        
        // Recent activities
        stats.setRecentActivities(getRecentActivities(userId));
        
        // Alerts
        stats.setAlerts(getAlerts(userId));
        
        return stats;
    }
    
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        LocalDate endOfWeek = LocalDate.now();
        
        // Weekly hours
        Double weeklyHours = timeEntryRepository.calculateTotalHoursForUserInDateRange(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.put("weeklyHours", weeklyHours != null ? weeklyHours : 0.0);
        
        // Monthly hours
        LocalDate startOfMonth = LocalDate.now().minusDays(30);
        Double monthlyHours = timeEntryRepository.calculateTotalHoursForUserInDateRange(
                userId, startOfMonth.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.put("monthlyHours", monthlyHours != null ? monthlyHours : 0.0);
        
        // Overtime
        Double overtime = timeEntryRepository.calculateTotalOvertimeForUserInDateRange(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.put("overtimeHours", overtime != null ? overtime : 0.0);
        
        // Average hours per day
        Double avgHoursPerDay = timeEntryRepository.calculateAverageHoursPerDay(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.put("averageHoursPerDay", avgHoursPerDay != null ? avgHoursPerDay : 0.0);
        
        // Active tasks
        long activeTasks = taskRepository.countByUserAndStatus(userId, com.teamlogger.backend.entity.Task.TaskStatus.IN_PROGRESS);
        stats.put("activeTasks", activeTasks);
        
        // Completed tasks this week
        long completedTasksThisWeek = taskRepository.countCompletedTasksByUserInDateRange(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        stats.put("completedTasksThisWeek", completedTasksThisWeek);
        
        return stats;
    }
    
    public Map<String, Object> getTeamStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Get all active users
        List<User> activeUsers = userRepository.findByIsActive(true);
        stats.put("totalTeamMembers", activeUsers.size());
        stats.put("onlineTeamMembers", activeUsers.stream().filter(User::isOnline).count());
        
        // Team productivity
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        LocalDate endOfWeek = LocalDate.now();
        
        Map<String, Double> productivityByUser = new HashMap<>();
        for (User user : activeUsers) {
            Double userHours = timeEntryRepository.calculateTotalHoursForUserInDateRange(
                    user.getId(), startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
            productivityByUser.put(user.getFullName(), userHours != null ? userHours : 0.0);
        }
        stats.put("productivityByUser", productivityByUser);
        
        // Team average hours
        Double teamTotalHours = activeUsers.stream()
                .mapToDouble(user -> {
                    Double hours = timeEntryRepository.calculateTotalHoursForUserInDateRange(
                            user.getId(), startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
                    return hours != null ? hours : 0.0;
                })
                .sum();
        stats.put("teamTotalHours", teamTotalHours);
        stats.put("teamAverageHours", activeUsers.size() > 0 ? teamTotalHours / activeUsers.size() : 0.0);
        
        return stats;
    }
    
    public Map<String, Object> getProjectStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Project counts by status
        stats.put("totalProjects", projectRepository.count());
        stats.put("activeProjects", projectRepository.countByStatus(com.teamlogger.backend.entity.Project.ProjectStatus.ACTIVE));
        stats.put("completedProjects", projectRepository.countByStatus(com.teamlogger.backend.entity.Project.ProjectStatus.COMPLETED));
        stats.put("planningProjects", projectRepository.countByStatus(com.teamlogger.backend.entity.Project.ProjectStatus.PLANNING));
        
        // Hours by project
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        LocalDate endOfWeek = LocalDate.now();
        
        Map<String, Double> hoursByProject = new HashMap<>();
        // This would need to be implemented based on your specific requirements
        stats.put("hoursByProject", hoursByProject);
        
        // Average project progress
        Double avgProgress = projectRepository.calculateAverageProjectProgress();
        stats.put("averageProjectProgress", avgProgress != null ? avgProgress : 0.0);
        
        return stats;
    }
    
    public List<DashboardStatsDto.RecentActivityDto> getRecentActivities(Long userId) {
        List<DashboardStatsDto.RecentActivityDto> activities = new ArrayList<>();
        
        // Get recent time entries
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<com.teamlogger.backend.entity.TimeEntry> recentTimeEntries = 
                timeEntryRepository.findRecentTimeEntries(userId, startDate, LocalDateTime.now());
        
        for (com.teamlogger.backend.entity.TimeEntry entry : recentTimeEntries) {
            DashboardStatsDto.RecentActivityDto activity = new DashboardStatsDto.RecentActivityDto();
            activity.setType("TIME_ENTRY");
            activity.setDescription("Logged " + entry.getTotalWorkHours() + " hours");
            activity.setUserName(entry.getUser().getFullName());
            activity.setTimestamp(entry.getPunchInTime().toString());
            activities.add(activity);
        }
        
        // Sort by timestamp (most recent first)
        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        return activities.stream().limit(10).collect(Collectors.toList());
    }
    
    public List<String> getAlerts(Long userId) {
        List<String> alerts = new ArrayList<>();
        
        // Check for overtime
        LocalDate startOfWeek = LocalDate.now().minusDays(7);
        LocalDate endOfWeek = LocalDate.now();
        Double overtime = timeEntryRepository.calculateTotalOvertimeForUserInDateRange(
                userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        
        if (overtime != null && overtime > 5.0) {
            alerts.add("You have worked " + overtime + " hours of overtime this week");
        }
        
        // Check for overdue tasks
        long overdueTasks = taskRepository.findOverdueTasksByUser(userId, LocalDateTime.now()).size();
        if (overdueTasks > 0) {
            alerts.add("You have " + overdueTasks + " overdue tasks");
        }
        
        return alerts;
    }
    
    public Map<String, Object> getProductivityChart(Long userId, String period) {
        Map<String, Object> chartData = new HashMap<>();
        List<DashboardStatsDto.ChartDataPoint> dataPoints = new ArrayList<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        switch (period.toLowerCase()) {
            case "week":
                startDate = endDate.minusDays(7);
                break;
            case "month":
                startDate = endDate.minusDays(30);
                break;
            default:
                startDate = endDate.minusDays(7);
        }
        
        // Generate data points for each day
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DashboardStatsDto.ChartDataPoint point = new DashboardStatsDto.ChartDataPoint();
            point.setLabel(currentDate.toString());
            point.setDate(currentDate.toString());
            
            Double hours = timeEntryRepository.calculateTotalHoursForUserInDateRange(
                    userId, currentDate.atStartOfDay(), currentDate.atTime(23, 59, 59));
            point.setValue(hours != null ? hours : 0.0);
            
            dataPoints.add(point);
            currentDate = currentDate.plusDays(1);
        }
        
        chartData.put("dataPoints", dataPoints);
        chartData.put("period", period);
        
        return chartData;
    }
} 