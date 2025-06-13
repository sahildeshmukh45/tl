package com.teamlogger.backend.controller;

import com.teamlogger.backend.entity.TimeEntry;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.repository.TimeEntryRepository;
import com.teamlogger.backend.repository.UserRepository;
import com.teamlogger.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/timesheet")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<TimeEntry>> getTimesheet(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        
        Long targetUserId = userId;
        if (targetUserId == null) {
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            targetUserId = currentUser.getId();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<TimeEntry> timeEntries = timeEntryRepository.findUserTimeEntriesInDateRange(
                targetUserId, startDateTime, endDateTime);
        
        return ResponseEntity.ok(timeEntries);
    }
    
    @GetMapping("/timesheet/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<byte[]> exportTimesheetExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        
        Long targetUserId = userId;
        if (targetUserId == null) {
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            targetUserId = currentUser.getId();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<TimeEntry> timeEntries = timeEntryRepository.findUserTimeEntriesInDateRange(
                targetUserId, startDateTime, endDateTime);
        
        // Generate Excel file
        byte[] excelData = generateExcelReport(timeEntries, startDate, endDate);
        
        String filename = "timesheet_" + startDate + "_to_" + endDate + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }
    
    @GetMapping("/timesheet/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<byte[]> exportTimesheetPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        
        Long targetUserId = userId;
        if (targetUserId == null) {
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            targetUserId = currentUser.getId();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<TimeEntry> timeEntries = timeEntryRepository.findUserTimeEntriesInDateRange(
                targetUserId, startDateTime, endDateTime);
        
        // Generate PDF file
        byte[] pdfData = generatePdfReport(timeEntries, startDate, endDate);
        
        String filename = "timesheet_" + startDate + "_to_" + endDate + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
    
    @PostMapping("/timesheet/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<String> emailTimesheet(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam String emailTo,
            Authentication authentication) {
        
        Long targetUserId = userId;
        if (targetUserId == null) {
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            targetUserId = currentUser.getId();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<TimeEntry> timeEntries = timeEntryRepository.findUserTimeEntriesInDateRange(
                targetUserId, startDateTime, endDateTime);
        
        // Generate and email report
        byte[] reportData = generateExcelReport(timeEntries, startDate, endDate);
        String filename = "timesheet_" + startDate + "_to_" + endDate + ".xlsx";
        
        emailService.sendTimesheetReport(emailTo, startDate, endDate, reportData, filename);
        
        return ResponseEntity.ok("Timesheet report sent successfully");
    }
    
    @GetMapping("/productivity")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getProductivityReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Get all users
        List<User> users = userRepository.findByIsActive(true);
        
        Map<String, Object> productivityData = Map.of(
                "startDate", startDate,
                "endDate", endDate,
                "totalUsers", users.size(),
                "userProductivity", generateUserProductivityData(users, startDateTime, endDateTime),
                "summary", generateProductivitySummary(users, startDateTime, endDateTime)
        );
        
        return ResponseEntity.ok(productivityData);
    }
    
    @GetMapping("/productivity/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> exportProductivityExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<User> users = userRepository.findByIsActive(true);
        
        // Generate Excel file
        byte[] excelData = generateProductivityExcelReport(users, startDateTime, endDateTime);
        
        String filename = "productivity_report_" + startDate + "_to_" + endDate + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }
    
    @GetMapping("/overtime")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TimeEntry>> getOvertimeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Get time entries with overtime
        List<TimeEntry> overtimeEntries = timeEntryRepository.findOvertimeEntriesInDateRange(
                startDateTime, endDateTime);
        
        return ResponseEntity.ok(overtimeEntries);
    }
    
    // Helper methods for report generation
    private byte[] generateExcelReport(List<TimeEntry> timeEntries, LocalDate startDate, LocalDate endDate) {
        // Implementation for Excel generation
        // This would use Apache POI to create Excel file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Excel generation logic here
        return outputStream.toByteArray();
    }
    
    private byte[] generatePdfReport(List<TimeEntry> timeEntries, LocalDate startDate, LocalDate endDate) {
        // Implementation for PDF generation
        // This would use iText to create PDF file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // PDF generation logic here
        return outputStream.toByteArray();
    }
    
    private Map<String, Object> generateUserProductivityData(List<User> users, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Implementation for user productivity data
        return Map.of("placeholder", "productivity data");
    }
    
    private Map<String, Object> generateProductivitySummary(List<User> users, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Implementation for productivity summary
        return Map.of("placeholder", "summary data");
    }
    
    private byte[] generateProductivityExcelReport(List<User> users, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Implementation for productivity Excel report
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Excel generation logic here
        return outputStream.toByteArray();
    }
} 