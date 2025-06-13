package com.teamlogger.backend.repository;

import com.teamlogger.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByManagerId(Long managerId);
    
    List<Project> findByStatus(Project.ProjectStatus status);
    
    List<Project> findByPriority(Project.ProjectPriority priority);
    
    List<Project> findByIsArchived(boolean isArchived);
    
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId")
    List<Project> findProjectsByMemberId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Project p WHERE p.deadline < :currentDate AND p.status != 'COMPLETED' AND p.status != 'CANCELLED'")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT p FROM Project p WHERE p.endDate BETWEEN :startDate AND :endDate")
    List<Project> findProjectsEndingBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Project p WHERE p.name LIKE %:searchTerm% OR p.description LIKE %:searchTerm% OR p.clientName LIKE %:searchTerm%")
    List<Project> searchProjects(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    Long countProjectsByStatus(@Param("status") Project.ProjectStatus status);
    
    @Query("SELECT AVG(p.progressPercentage) FROM Project p WHERE p.status != 'CANCELLED'")
    Double calculateAverageProjectProgress();
    
    @Query("SELECT p FROM Project p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Project> findProjectsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Project p WHERE p.progressPercentage >= 100 AND p.status != 'COMPLETED'")
    List<Project> findCompletedProjectsNotMarkedAsCompleted();
} 