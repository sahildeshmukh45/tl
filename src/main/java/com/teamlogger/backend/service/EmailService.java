package com.teamlogger.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - Team Logger");
        message.setText("You have requested to reset your password. Please use the following token to reset your password:\n\n" +
                "Reset Token: " + resetToken + "\n\n" +
                "This token will expire in 24 hours.\n\n" +
                "If you did not request this password reset, please ignore this email.");
        
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String toEmail, String fullName, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset - Team Logger");
        message.setText("Hello " + fullName + ",\n\n" +
                "Your password has been reset by an administrator.\n\n" +
                "New Password: " + newPassword + "\n\n" +
                "Please change your password after logging in.\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
    }
    
    public void sendWelcomeEmail(String toEmail, String fullName, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Team Logger");
        message.setText("Hello " + fullName + ",\n\n" +
                "Welcome to Team Logger! Your account has been successfully created.\n\n" +
                "Username: " + username + "\n\n" +
                "You can now log in to your account and start tracking your time.\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
    }
    
    public void sendTimeEntryApprovalEmail(String toEmail, String approverName, String date) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Time Entry Approved");
        message.setText("Your time entry for " + date + " has been approved by " + approverName + ".\n\n" +
                "Thank you for using Team Logger!");
        
        mailSender.send(message);
    }
    
    public void sendTimeEntryRejectionEmail(String toEmail, String approverName, String date, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Time Entry Requires Attention");
        message.setText("Your time entry for " + date + " requires attention.\n\n" +
                "Approver: " + approverName + "\n" +
                "Reason: " + reason + "\n\n" +
                "Please review and update your time entry accordingly.");
        
        mailSender.send(message);
    }
    
    public void sendWeeklyReportEmail(String toEmail, String userName, String reportContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Weekly Time Report - " + userName);
        message.setText("Weekly Time Report for " + userName + "\n\n" + reportContent);
        
        mailSender.send(message);
    }
    
    public void sendProjectAssignmentEmail(String toEmail, String userName, String projectName, String managerName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Project Assignment");
        message.setText("Hello " + userName + ",\n\n" +
                "You have been assigned to the project: " + projectName + "\n" +
                "Project Manager: " + managerName + "\n\n" +
                "Please log in to Team Logger to view project details and start tracking your time.\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
    }
    
    public void sendTaskAssignmentEmail(String toEmail, String userName, String taskTitle, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Task Assignment");
        message.setText("Hello " + userName + ",\n\n" +
                "You have been assigned a new task: " + taskTitle + "\n" +
                "Project: " + projectName + "\n\n" +
                "Please log in to Team Logger to view task details and update your progress.\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
    }
    
    public void sendOvertimeAlertEmail(String toEmail, String userName, Double overtimeHours) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Overtime Alert");
        message.setText("Hello " + userName + ",\n\n" +
                "You have worked " + overtimeHours + " hours of overtime this week.\n\n" +
                "Please ensure this is necessary and approved by your manager.\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
    }
    
    public void sendIdleTimeAlertEmail(String toEmail, String userName, Long idleMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Idle Time Alert");
        message.setText("Hello " + userName + ",\n\n" +
                "You have been idle for " + idleMinutes + " minutes.\n\n" +
                "Please ensure you are actively working or take a break if needed.\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
    }
    
    public void sendTimesheetReport(String toEmail, LocalDate startDate, LocalDate endDate, byte[] reportData, String filename) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Timesheet Report - " + startDate + " to " + endDate);
        message.setText("Please find attached your timesheet report for the period " + startDate + " to " + endDate + ".\n\n" +
                "Best regards,\nTeam Logger Team");
        
        mailSender.send(message);
        // Note: For actual file attachment, you would need to use JavaMailSender with MimeMessage
    }
} 