package com.teamlogger.backend.controller;

import com.teamlogger.backend.dto.TimeEntryDto;
import com.teamlogger.backend.entity.TimeEntry;
import com.teamlogger.backend.security.JwtTokenProvider;
import com.teamlogger.backend.service.TimeTrackingService;
import com.teamlogger.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/time-tracking")
@CrossOrigin(origins = "*")
public class TimeTrackingController {
    
    @Autowired
    private TimeTrackingService timeTrackingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/punch-in")
    public ResponseEntity<?> punchIn(@RequestHeader("Authorization") String token,
                                   @RequestParam(required = false) Long projectId,
                                   @RequestParam(required = false) Long taskId,
                                   @RequestParam(required = false) String notes) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.punchIn(userId, projectId, taskId, notes);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/punch-out")
    public ResponseEntity<?> punchOut(@RequestHeader("Authorization") String token,
                                    @RequestParam(required = false) String notes) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.punchOut(userId, notes);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/lunch/start")
    public ResponseEntity<?> startLunch(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.startLunch(userId);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/lunch/end")
    public ResponseEntity<?> endLunch(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.endLunch(userId);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/break/start")
    public ResponseEntity<?> startBreak(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.startBreak(userId);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/break/end")
    public ResponseEntity<?> endBreak(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.endBreak(userId);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/manual-entry")
    public ResponseEntity<?> createManualEntry(@RequestHeader("Authorization") String token,
                                             @Valid @RequestBody TimeEntryDto timeEntryDto) {
        try {
            Long userId = getUserIdFromToken(token);
            timeEntryDto.setUserId(userId);
            TimeEntry timeEntry = timeTrackingService.createManualEntry(timeEntryDto);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentTimeEntry(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.getCurrentTimeEntry(userId);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/entries")
    public ResponseEntity<?> getUserTimeEntries(@RequestHeader("Authorization") String token,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Long userId = getUserIdFromToken(token);
            List<TimeEntry> timeEntries = timeTrackingService.getUserTimeEntries(userId, startDate, endDate);
            return ResponseEntity.ok(timeEntries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/total-hours")
    public ResponseEntity<?> getUserTotalHours(@RequestHeader("Authorization") String token,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Long userId = getUserIdFromToken(token);
            Double totalHours = timeTrackingService.getUserTotalHours(userId, startDate, endDate);
            return ResponseEntity.ok(Map.of("totalHours", totalHours));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/overtime-hours")
    public ResponseEntity<?> getUserOvertimeHours(@RequestHeader("Authorization") String token,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Long userId = getUserIdFromToken(token);
            Double overtimeHours = timeTrackingService.getUserOvertimeHours(userId, startDate, endDate);
            return ResponseEntity.ok(Map.of("overtimeHours", overtimeHours));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/pending-approval")
    public ResponseEntity<?> getPendingApprovalTimeEntries(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<TimeEntry> timeEntries = timeTrackingService.getPendingApprovalTimeEntries(userId);
            return ResponseEntity.ok(timeEntries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/approve/{timeEntryId}")
    public ResponseEntity<?> approveTimeEntry(@RequestHeader("Authorization") String token,
                                            @PathVariable Long timeEntryId) {
        try {
            Long approvedBy = getUserIdFromToken(token);
            TimeEntry timeEntry = timeTrackingService.approveTimeEntry(timeEntryId, approvedBy);
            return ResponseEntity.ok(timeEntry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{timeEntryId}")
    public ResponseEntity<?> deleteTimeEntry(@RequestHeader("Authorization") String token,
                                           @PathVariable Long timeEntryId) {
        try {
            timeTrackingService.deleteTimeEntry(timeEntryId);
            return ResponseEntity.ok().body(Map.of("message", "Time entry deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private Long getUserIdFromToken(String token) {
        String username = tokenProvider.getUsernameFromToken(token.substring(7));
        return userService.loadUserByUsername(username).getId();
    }
} 