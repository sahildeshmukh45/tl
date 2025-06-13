package com.teamlogger.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.teamlogger.backend.entity.Screenshot;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.entity.TimeEntry;
import com.teamlogger.backend.repository.ScreenshotRepository;
import com.teamlogger.backend.repository.UserRepository;
import com.teamlogger.backend.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScreenshotService {
    
    @Autowired
    private ScreenshotRepository screenshotRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private Cloudinary cloudinary;
    
    @Value("${screenshot.interval:300000}")
    private long screenshotInterval;
    
    @Value("${screenshot.enabled:true}")
    private boolean screenshotEnabled;
    
    @Value("${screenshot.quality:0.8}")
    private double screenshotQuality;
    
    // Track active screenshot sessions
    private final ConcurrentHashMap<Long, ScreenshotSession> activeSessions = new ConcurrentHashMap<>();
    
    public void startScreenshotCapture(Long userId, Long timeEntryId) {
        if (!screenshotEnabled) {
            return;
        }
        
        ScreenshotSession session = new ScreenshotSession(userId, timeEntryId);
        activeSessions.put(userId, session);
        
        // Start the screenshot capture thread
        session.startCapture();
    }
    
    public void stopScreenshotCapture(Long userId) {
        ScreenshotSession session = activeSessions.remove(userId);
        if (session != null) {
            session.stopCapture();
        }
    }
    
    public Screenshot captureManualScreenshot(Long userId, Long timeEntryId, String notes) {
        try {
            BufferedImage screenshot = captureScreen();
            String cloudinaryUrl = uploadToCloudinary(screenshot, userId);
            
            Screenshot screenshotEntity = new Screenshot();
            screenshotEntity.setUser(userRepository.findById(userId).orElse(null));
            screenshotEntity.setTimeEntry(timeEntryRepository.findById(timeEntryId).orElse(null));
            screenshotEntity.setCloudinaryUrl(cloudinaryUrl);
            screenshotEntity.setFileName("manual_screenshot_" + System.currentTimeMillis() + ".png");
            screenshotEntity.setFileSize((long) screenshot.getWidth() * screenshot.getHeight() * 4);
            screenshotEntity.setImageWidth(screenshot.getWidth());
            screenshotEntity.setImageHeight(screenshot.getHeight());
            screenshotEntity.setCapturedAt(LocalDateTime.now());
            screenshotEntity.setIsManual(true);
            screenshotEntity.setNotes(notes);
            
            return screenshotRepository.save(screenshotEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to capture manual screenshot", e);
        }
    }
    
    private BufferedImage captureScreen() throws AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
        
        // Compress the image
        BufferedImage compressed = new BufferedImage(
                (int) (capture.getWidth() * screenshotQuality),
                (int) (capture.getHeight() * screenshotQuality),
                BufferedImage.TYPE_INT_RGB
        );
        
        Graphics2D g2d = compressed.createGraphics();
        g2d.drawImage(capture, 0, 0, compressed.getWidth(), compressed.getHeight(), null);
        g2d.dispose();
        
        return compressed;
    }
    
    private String uploadToCloudinary(BufferedImage image, Long userId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    new ByteArrayInputStream(imageBytes),
                    ObjectUtils.asMap(
                            "public_id", "teamlogger/screenshots/user_" + userId + "_" + System.currentTimeMillis(),
                            "folder", "teamlogger/screenshots",
                            "resource_type", "image"
                    )
            );
            
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload screenshot to Cloudinary", e);
        }
    }
    
    public List<Screenshot> getUserScreenshots(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return screenshotRepository.findScreenshotsByUserInDateRange(userId, startDate, endDate);
    }
    
    public List<Screenshot> getTimeEntryScreenshots(Long timeEntryId) {
        return screenshotRepository.findScreenshotsByTimeEntryOrdered(timeEntryId);
    }
    
    public void deleteScreenshot(Long screenshotId) {
        Screenshot screenshot = screenshotRepository.findById(screenshotId)
                .orElseThrow(() -> new RuntimeException("Screenshot not found"));
        
        // Delete from Cloudinary
        try {
            if (screenshot.getCloudinaryPublicId() != null) {
                cloudinary.uploader().destroy(screenshot.getCloudinaryPublicId(), ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            // Log error but continue with database deletion
            System.err.println("Failed to delete screenshot from Cloudinary: " + e.getMessage());
        }
        
        screenshotRepository.delete(screenshot);
    }
    
    public Screenshot approveScreenshot(Long screenshotId, Long approvedBy) {
        Screenshot screenshot = screenshotRepository.findById(screenshotId)
                .orElseThrow(() -> new RuntimeException("Screenshot not found"));
        
        screenshot.setIsApproved(true);
        screenshot.setApprovedBy(approvedBy);
        screenshot.setApprovedAt(LocalDateTime.now());
        
        return screenshotRepository.save(screenshot);
    }
    
    // Inner class to manage screenshot sessions
    private class ScreenshotSession {
        private final Long userId;
        private final Long timeEntryId;
        private volatile boolean running = false;
        private Thread captureThread;
        
        public ScreenshotSession(Long userId, Long timeEntryId) {
            this.userId = userId;
            this.timeEntryId = timeEntryId;
        }
        
        public void startCapture() {
            running = true;
            captureThread = new Thread(() -> {
                while (running) {
                    try {
                        Thread.sleep(screenshotInterval);
                        if (running) {
                            captureAndSaveScreenshot();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Error capturing screenshot: " + e.getMessage());
                    }
                }
            });
            captureThread.start();
        }
        
        public void stopCapture() {
            running = false;
            if (captureThread != null) {
                captureThread.interrupt();
            }
        }
        
        private void captureAndSaveScreenshot() {
            try {
                BufferedImage screenshot = captureScreen();
                String cloudinaryUrl = uploadToCloudinary(screenshot, userId);
                
                Screenshot screenshotEntity = new Screenshot();
                screenshotEntity.setUser(userRepository.findById(userId).orElse(null));
                screenshotEntity.setTimeEntry(timeEntryRepository.findById(timeEntryId).orElse(null));
                screenshotEntity.setCloudinaryUrl(cloudinaryUrl);
                screenshotEntity.setFileName("auto_screenshot_" + System.currentTimeMillis() + ".png");
                screenshotEntity.setFileSize((long) screenshot.getWidth() * screenshot.getHeight() * 4);
                screenshotEntity.setImageWidth(screenshot.getWidth());
                screenshotEntity.setImageHeight(screenshot.getHeight());
                screenshotEntity.setCapturedAt(LocalDateTime.now());
                screenshotEntity.setIsManual(false);
                
                screenshotRepository.save(screenshotEntity);
            } catch (Exception e) {
                System.err.println("Failed to capture and save screenshot: " + e.getMessage());
            }
        }
    }
} 