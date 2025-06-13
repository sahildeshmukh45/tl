package com.teamlogger.backend.repository;

import com.teamlogger.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByResetToken(String resetToken);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByIsActive(boolean isActive);
    
    List<User> findByIsOnline(boolean isOnline);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :cutoffDate")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isOnline = true")
    Long countOnlineUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = :isActive")
    Long countByIsActive(boolean isActive);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 