package com.teamlogger.backend.repository;

import com.teamlogger.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByProjectId(Long projectId);
    
    List<Task> findByAssignedToId(Long userId);
    
    List<Task> findByCreatedById(Long userId);
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByPriority(Task.TaskPriority priority);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.status != 'COMPLETED'")
    List<Task> findActiveTasksByUser(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status != 'COMPLETED'")
    List<Task> findActiveTasksByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:searchTerm% OR t.description LIKE %:searchTerm%")
    List<Task> searchTasks(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    Long countByStatus(@Param("status") Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId AND t.status = :status")
    Long countByUserAndStatus(@Param("userId") Long userId, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT AVG(t.progressPercentage) FROM Task t WHERE t.project.id = :projectId")
    Double calculateAverageTaskProgressByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<Task> findTasksCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId AND t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasksByUser(@Param("userId") Long userId, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT SUM(t.actualHours) FROM Task t WHERE t.assignedTo.id = :userId AND t.status = 'COMPLETED'")
    Double calculateTotalCompletedHoursByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :userId AND t.status = 'COMPLETED' AND t.completedAt >= :startDate AND t.completedAt <= :endDate")
    Long countCompletedTasksByUserInDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 