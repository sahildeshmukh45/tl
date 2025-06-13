package com.teamlogger.backend.service;

import com.teamlogger.backend.dto.TimeEntryDto;
import com.teamlogger.backend.entity.TimeEntry;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.entity.Project;
import com.teamlogger.backend.entity.Task;
import com.teamlogger.backend.repository.TimeEntryRepository;
import com.teamlogger.backend.repository.UserRepository;
import com.teamlogger.backend.repository.ProjectRepository;
import com.teamlogger.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TimeTrackingService {
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ScreenshotService screenshotService;
    
    public TimeEntry punchIn(Long userId, Long projectId, Long taskId, String notes) {
        // Check if user already has an active time entry
        Optional<TimeEntry> activeEntry = timeEntryRepository.findByUserIdAndIsActiveTrue(userId);
        if (activeEntry.isPresent()) {
            throw new RuntimeException("User already has an active time entry");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setUser(user);
        timeEntry.setPunchInTime(LocalDateTime.now());
        timeEntry.setIsActive(true);
        timeEntry.setNotes(notes);
        
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            timeEntry.setProject(project);
        }
        
        if (taskId != null) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            timeEntry.setTask(task);
        }
        
        TimeEntry savedEntry = timeEntryRepository.save(timeEntry);
        
        // Start screenshot capture for this time entry
        screenshotService.startScreenshotCapture(userId, savedEntry.getId());
        
        return savedEntry;
    }
    
    public TimeEntry punchOut(Long userId, String notes) {
        TimeEntry timeEntry = timeEntryRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active time entry found"));
        
        timeEntry.setPunchOutTime(LocalDateTime.now());
        timeEntry.setIsActive(false);
        if (notes != null) {
            timeEntry.setNotes(timeEntry.getNotes() != null ? 
                    timeEntry.getNotes() + "\n" + notes : notes);
        }
        
        timeEntry.calculateHours();
        
        // Stop screenshot capture
        screenshotService.stopScreenshotCapture(userId);
        
        return timeEntryRepository.save(timeEntry);
    }
    
    public TimeEntry startLunch(Long userId) {
        TimeEntry timeEntry = timeEntryRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active time entry found"));
        
        if (timeEntry.getLunchInTime() != null) {
            throw new RuntimeException("Lunch already started");
        }
        
        timeEntry.setLunchInTime(LocalDateTime.now());
        return timeEntryRepository.save(timeEntry);
    }
    
    public TimeEntry endLunch(Long userId) {
        TimeEntry timeEntry = timeEntryRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active time entry found"));
        
        if (timeEntry.getLunchInTime() == null) {
            throw new RuntimeException("Lunch not started");
        }
        
        if (timeEntry.getLunchOutTime() != null) {
            throw new RuntimeException("Lunch already ended");
        }
        
        timeEntry.setLunchOutTime(LocalDateTime.now());
        timeEntry.calculateHours();
        return timeEntryRepository.save(timeEntry);
    }
    
    public TimeEntry startBreak(Long userId) {
        TimeEntry timeEntry = timeEntryRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active time entry found"));
        
        if (timeEntry.getBreakStartTime() != null && timeEntry.getBreakEndTime() == null) {
            throw new RuntimeException("Break already started");
        }
        
        timeEntry.setBreakStartTime(LocalDateTime.now());
        return timeEntryRepository.save(timeEntry);
    }
    
    public TimeEntry endBreak(Long userId) {
        TimeEntry timeEntry = timeEntryRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("No active time entry found"));
        
        if (timeEntry.getBreakStartTime() == null) {
            throw new RuntimeException("Break not started");
        }
        
        if (timeEntry.getBreakEndTime() != null) {
            throw new RuntimeException("Break already ended");
        }
        
        timeEntry.setBreakEndTime(LocalDateTime.now());
        timeEntry.calculateHours();
        return timeEntryRepository.save(timeEntry);
    }
    
    public TimeEntry createManualEntry(TimeEntryDto timeEntryDto) {
        User user = userRepository.findById(timeEntryDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setUser(user);
        timeEntry.setPunchInTime(timeEntryDto.getPunchInTime());
        timeEntry.setPunchOutTime(timeEntryDto.getPunchOutTime());
        timeEntry.setLunchInTime(timeEntryDto.getLunchInTime());
        timeEntry.setLunchOutTime(timeEntryDto.getLunchOutTime());
        timeEntry.setBreakStartTime(timeEntryDto.getBreakStartTime());
        timeEntry.setBreakEndTime(timeEntryDto.getBreakEndTime());
        timeEntry.setNotes(timeEntryDto.getNotes());
        timeEntry.setIsManualEntry(true);
        timeEntry.setIsActive(false);
        
        if (timeEntryDto.getProjectId() != null) {
            Project project = projectRepository.findById(timeEntryDto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            timeEntry.setProject(project);
        }
        
        if (timeEntryDto.getTaskId() != null) {
            Task task = taskRepository.findById(timeEntryDto.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            timeEntry.setTask(task);
        }
        
        timeEntry.calculateHours();
        return timeEntryRepository.save(timeEntry);
    }
    
    public List<TimeEntry> getUserTimeEntries(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        return timeEntryRepository.findUserTimeEntriesInDateRange(userId, startDateTime, endDateTime);
    }
    
    public Double getUserTotalHours(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Double totalHours = timeEntryRepository.calculateTotalHoursForUserInDateRange(userId, startDateTime, endDateTime);
        return totalHours != null ? totalHours : 0.0;
    }
    
    public Double getUserOvertimeHours(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Double overtimeHours = timeEntryRepository.calculateTotalOvertimeForUserInDateRange(userId, startDateTime, endDateTime);
        return overtimeHours != null ? overtimeHours : 0.0;
    }
    
    public TimeEntry getCurrentTimeEntry(Long userId) {
        return timeEntryRepository.findByUserIdAndIsActiveTrue(userId)
                .orElse(null);
    }
    
    public List<TimeEntry> getPendingApprovalTimeEntries(Long userId) {
        LocalDateTime startDate = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atTime(23, 59, 59);
        
        return timeEntryRepository.findPendingApprovalTimeEntries(userId, startDate, endDate);
    }
    
    public TimeEntry approveTimeEntry(Long timeEntryId, Long approvedBy) {
        TimeEntry timeEntry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new RuntimeException("Time entry not found"));
        
        timeEntry.setIsApproved(true);
        timeEntry.setApprovedBy(approvedBy);
        timeEntry.setApprovedAt(LocalDateTime.now());
        
        return timeEntryRepository.save(timeEntry);
    }
    
    public void deleteTimeEntry(Long timeEntryId) {
        TimeEntry timeEntry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new RuntimeException("Time entry not found"));
        
        if (timeEntry.getIsActive()) {
            throw new RuntimeException("Cannot delete active time entry");
        }
        
        timeEntryRepository.delete(timeEntry);
    }
} 