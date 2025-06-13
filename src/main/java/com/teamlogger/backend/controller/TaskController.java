package com.teamlogger.backend.controller;

import com.teamlogger.backend.entity.Task;
import com.teamlogger.backend.entity.User;
import com.teamlogger.backend.repository.TaskRepository;
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
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        task.setCreatedBy(currentUser);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        Task savedTask = taskRepository.save(task);
        
        // Send email notification to assigned user
        if (task.getAssignedTo() != null) {
            emailService.sendTaskAssignmentEmail(
                    task.getAssignedTo().getEmail(),
                    task.getAssignedTo().getFullName(),
                    task.getTitle(),
                    task.getProject() != null ? task.getProject().getName() : "No Project"
            );
        }
        
        return ResponseEntity.ok(savedTask);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setPriority(taskDetails.getPriority());
            task.setStatus(taskDetails.getStatus());
            task.setProject(taskDetails.getProject());
            task.setAssignedTo(taskDetails.getAssignedTo());
            task.setDueDate(taskDetails.getDueDate());
            task.setEstimatedHours(taskDetails.getEstimatedHours());
            task.setActualHours(taskDetails.getActualHours());
            task.setProgressPercentage(taskDetails.getProgressPercentage());
            task.setTags(taskDetails.getTags());
            task.setUpdatedAt(LocalDateTime.now());
            
            Task updatedTask = taskRepository.save(task);
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/assigned/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable Long userId) {
        List<Task> tasks = taskRepository.findByAssignedToId(userId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/created/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Task>> getTasksByCreator(@PathVariable Long userId) {
        List<Task> tasks = taskRepository.findByCreatedById(userId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        List<Task> tasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String searchTerm) {
        List<Task> tasks = taskRepository.searchTasks(searchTerm);
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Task> startTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            task.setStartedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            Task startedTask = taskRepository.save(task);
            return ResponseEntity.ok(startedTask);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setProgressPercentage(100);
            task.setCompletedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            Task completedTask = taskRepository.save(task);
            return ResponseEntity.ok(completedTask);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Task> updateTaskProgress(@PathVariable Long id, @RequestParam Integer progress) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setProgressPercentage(progress);
            task.setUpdatedAt(LocalDateTime.now());
            
            // Update status based on progress
            if (progress >= 100) {
                task.setStatus(Task.TaskStatus.COMPLETED);
                task.setCompletedAt(LocalDateTime.now());
            } else if (progress > 0) {
                task.setStatus(Task.TaskStatus.IN_PROGRESS);
                if (task.getStartedAt() == null) {
                    task.setStartedAt(LocalDateTime.now());
                }
            }
            
            Task updatedTask = taskRepository.save(task);
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> getTaskStats() {
        return ResponseEntity.ok(Map.of(
                "totalTasks", taskRepository.count(),
                "completedTasks", taskRepository.countByStatus(Task.TaskStatus.COMPLETED),
                "inProgressTasks", taskRepository.countByStatus(Task.TaskStatus.IN_PROGRESS),
                "overdueTasks", taskRepository.findOverdueTasks(LocalDateTime.now()).size()
        ));
    }
} 