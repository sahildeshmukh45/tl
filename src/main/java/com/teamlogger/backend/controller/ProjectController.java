package com.teamlogger.backend.controller;

import com.teamlogger.backend.entity.Project;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.repository.ProjectRepository;
import com.teamlogger.backend.repository.UserRepository;
import com.teamlogger.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Project> createProject(@RequestBody Project project, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        project.setManager(currentUser);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        Project savedProject = projectRepository.save(project);
        
        // Send email notifications to team members
        if (project.getMembers() != null) {
            for (User member : project.getMembers()) {
                emailService.sendProjectAssignmentEmail(
                        member.getEmail(),
                        member.getFullName(),
                        project.getName(),
                        currentUser.getFullName()
                );
            }
        }
        
        return ResponseEntity.ok(savedProject);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setName(projectDetails.getName());
            project.setDescription(projectDetails.getDescription());
            project.setPriority(projectDetails.getPriority());
            project.setStatus(projectDetails.getStatus());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setDeadline(projectDetails.getDeadline());
            project.setMembers(projectDetails.getMembers());
            project.setProgressPercentage(projectDetails.getProgressPercentage());
            project.setBudget(projectDetails.getBudget());
            project.setClientName(projectDetails.getClientName());
            project.setIsArchived(projectDetails.isIsArchived());
            project.setUpdatedAt(LocalDateTime.now());
            
            Project updatedProject = projectRepository.save(project);
            return ResponseEntity.ok(updatedProject);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Project>> getProjectsByManager(@PathVariable Long managerId) {
        List<Project> projects = projectRepository.findByManagerId(managerId);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/member/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Project>> getProjectsByMember(@PathVariable Long userId) {
        List<Project> projects = projectRepository.findProjectsByMemberId(userId);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable Project.ProjectStatus status) {
        List<Project> projects = projectRepository.findByStatus(status);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Project>> getOverdueProjects() {
        List<Project> projects = projectRepository.findOverdueProjects(LocalDateTime.now());
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam String searchTerm) {
        List<Project> projects = projectRepository.searchProjects(searchTerm);
        return ResponseEntity.ok(projects);
    }
    
    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Project> archiveProject(@PathVariable Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setIsArchived(true);
            project.setUpdatedAt(LocalDateTime.now());
            Project archivedProject = projectRepository.save(project);
            return ResponseEntity.ok(archivedProject);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/unarchive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Project> unarchiveProject(@PathVariable Long id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setIsArchived(false);
            project.setUpdatedAt(LocalDateTime.now());
            Project unarchivedProject = projectRepository.save(project);
            return ResponseEntity.ok(unarchivedProject);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> getProjectStats() {
        return ResponseEntity.ok(Map.of(
                "totalProjects", projectRepository.count(),
                "activeProjects", projectRepository.countByStatus(Project.ProjectStatus.ACTIVE),
                "completedProjects", projectRepository.countByStatus(Project.ProjectStatus.COMPLETED),
                "overdueProjects", projectRepository.findOverdueProjects(LocalDateTime.now()).size(),
                "averageProgress", projectRepository.calculateAverageProjectProgress()
        ));
    }
} 