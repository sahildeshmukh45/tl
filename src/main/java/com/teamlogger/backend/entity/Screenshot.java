package com.teamlogger.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "screenshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Screenshot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_entry_id")
    private TimeEntry timeEntry;
    
    @Column(name = "cloudinary_url", nullable = false)
    private String cloudinaryUrl;
    
    @Column(name = "cloudinary_public_id")
    private String cloudinaryPublicId;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "image_width")
    private Integer imageWidth;
    
    @Column(name = "image_height")
    private Integer imageHeight;
    
    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_manual")
    private boolean isManual = false;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "is_approved")
    private boolean isApproved = true;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (capturedAt == null) {
            capturedAt = LocalDateTime.now();
        }
    }
} 