package com.teamlogger.backend.controller;

import com.teamlogger.backend.dto.DashboardStatsDto;
import com.teamlogger.backend.service.DashboardService;
import com.teamlogger.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            DashboardStatsDto stats = dashboardService.getDashboardStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/user-stats")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Map<String, Object> userStats = dashboardService.getUserStats(userId);
            return ResponseEntity.ok(userStats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/team-stats")
    public ResponseEntity<?> getTeamStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Map<String, Object> teamStats = dashboardService.getTeamStats(userId);
            return ResponseEntity.ok(teamStats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/project-stats")
    public ResponseEntity<?> getProjectStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Map<String, Object> projectStats = dashboardService.getProjectStats(userId);
            return ResponseEntity.ok(projectStats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/recent-activities")
    public ResponseEntity<?> getRecentActivities(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            return ResponseEntity.ok(dashboardService.getRecentActivities(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/productivity-chart")
    public ResponseEntity<?> getProductivityChart(@RequestHeader("Authorization") String token,
                                                @RequestParam String period) {
        try {
            Long userId = getUserIdFromToken(token);
            return ResponseEntity.ok(dashboardService.getProductivityChart(userId, period));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/online-users")
    public ResponseEntity<?> getOnlineUsers(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(Map.of("onlineUsers", userService.getOnlineUserCount()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private Long getUserIdFromToken(String token) {
        String username = token.substring(7); // Remove "Bearer "
        return userService.loadUserByUsername(username).getId();
    }
} 