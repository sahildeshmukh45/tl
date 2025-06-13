package com.teamlogger.backend.repository;

import com.teamlogger.backend.entity.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    List<TimeEntry> findByUserId(Long userId);
    
    List<TimeEntry> findByUserIdAndPunchInTimeBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<TimeEntry> findByProjectId(Long projectId);
    
    List<TimeEntry> findByTaskId(Long taskId);
    
    Optional<TimeEntry> findByUserIdAndIsActiveTrue(Long userId);
    
    List<TimeEntry> findByIsActive(boolean isActive);
    
    List<TimeEntry> findByIsApproved(boolean isApproved);
    
    List<TimeEntry> findByIsManualEntry(boolean isManualEntry);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    List<TimeEntry> findUserTimeEntriesInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(te.totalWorkHours) FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    Double calculateTotalHoursForUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(te.overtimeHours) FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    Double calculateTotalOvertimeForUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    List<TimeEntry> findAllTimeEntriesInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate AND te.isApproved = false")
    List<TimeEntry> findPendingApprovalTimeEntries(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(te.totalWorkHours) FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    Double calculateAverageHoursPerDay(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate ORDER BY te.punchInTime DESC")
    List<TimeEntry> findRecentTimeEntries(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(te) FROM TimeEntry te WHERE te.user.id = :userId AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    Long countTimeEntriesForUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.overtimeHours > 0 AND te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    List<TimeEntry> findOvertimeEntriesInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(te.totalWorkHours) FROM TimeEntry te WHERE te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    Double calculateTotalHoursInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(te.overtimeHours) FROM TimeEntry te WHERE te.punchInTime >= :startDate AND te.punchInTime <= :endDate")
    Double calculateTotalOvertimeInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 