package com.teamlogger.backend.repository;

import com.teamlogger.backend.entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
    
    List<Screenshot> findByUserId(Long userId);
    
    List<Screenshot> findByTimeEntryId(Long timeEntryId);
    
    List<Screenshot> findByIsManual(boolean isManual);
    
    List<Screenshot> findByIsApproved(boolean isApproved);
    
    @Query("SELECT s FROM Screenshot s WHERE s.user.id = :userId AND s.capturedAt >= :startDate AND s.capturedAt <= :endDate")
    List<Screenshot> findScreenshotsByUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Screenshot s WHERE s.capturedAt >= :startDate AND s.capturedAt <= :endDate")
    List<Screenshot> findAllScreenshotsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM Screenshot s WHERE s.user.id = :userId AND s.capturedAt >= :startDate AND s.capturedAt <= :endDate")
    Long countScreenshotsByUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Screenshot s WHERE s.user.id = :userId AND s.isApproved = false")
    List<Screenshot> findPendingApprovalScreenshots(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Screenshot s WHERE s.capturedAt >= :startDate AND s.capturedAt <= :endDate ORDER BY s.capturedAt DESC")
    List<Screenshot> findRecentScreenshots(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Screenshot s WHERE s.user.id = :userId ORDER BY s.capturedAt DESC LIMIT :limit")
    List<Screenshot> findLatestScreenshotsByUser(@Param("userId") Long userId, @Param("limit") int limit);
    
    @Query("SELECT s FROM Screenshot s WHERE s.timeEntry.id = :timeEntryId ORDER BY s.capturedAt ASC")
    List<Screenshot> findScreenshotsByTimeEntryOrdered(@Param("timeEntryId") Long timeEntryId);
    
    @Query("SELECT s FROM Screenshot s WHERE s.user.id = :userId AND s.capturedAt >= :startDate AND s.capturedAt <= :endDate AND s.isManual = true")
    List<Screenshot> findManualScreenshotsByUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 