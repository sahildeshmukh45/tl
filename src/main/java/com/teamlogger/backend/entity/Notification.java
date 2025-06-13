package com.teamlogger.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.INFO;
    
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "action_url")
    private String actionUrl;
    
    @Column(name = "is_email_sent")
    private boolean isEmailSent = false;
    
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        INFO, WARNING, ERROR, SUCCESS, REMINDER
    }
    
    public enum NotificationStatus {
        UNREAD, READ, ARCHIVED
    }
} 