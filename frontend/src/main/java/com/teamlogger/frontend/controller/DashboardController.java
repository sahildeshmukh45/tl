package com.teamlogger.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamlogger.frontend.service.ApiService;
import com.teamlogger.frontend.service.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    @FXML
    private Text userNameLabel;
    
    @FXML
    private Text userRoleLabel;
    
    @FXML
    private Text pageTitleLabel;
    
    @FXML
    private Text currentTimeLabel;
    
    @FXML
    private Text statusLabel;
    
    @FXML
    private Text todayHoursLabel;
    
    @FXML
    private Text weekHoursLabel;
    
    @FXML
    private Button punchInButton;
    
    @FXML
    private Button punchOutButton;
    
    @FXML
    private Button breakButton;
    
    @FXML
    private TextArea notesTextArea;
    
    @FXML
    private Text activeProjectsLabel;
    
    @FXML
    private Text pendingTasksLabel;
    
    @FXML
    private Text overtimeLabel;
    
    @FXML
    private ListView<String> activityListView;
    
    @FXML
    private Button dashboardButton;
    
    @FXML
    private Button timeTrackingButton;
    
    @FXML
    private Button projectsButton;
    
    @FXML
    private Button tasksButton;
    
    @FXML
    private Button reportsButton;
    
    @FXML
    private Button usersButton;
    
    @FXML
    private Button settingsButton;
    
    @FXML
    private Button logoutButton;
    
    private final ApiService apiService = new ApiService();
    private final AuthService authService = new AuthService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Timer timer;
    private boolean isWorking = false;
    private LocalDateTime punchInTime;
    
    @FXML
    public void initialize() {
        // Set user information
        userNameLabel.setText("Welcome, " + authService.getFullName());
        userRoleLabel.setText(authService.getUserRole());
        
        // Show admin/manager options
        if (authService.isManager()) {
            usersButton.setVisible(true);
        }
        
        // Initialize activity list
        ObservableList<String> activities = FXCollections.observableArrayList();
        activityListView.setItems(activities);
        
        // Start timer for current time
        startTimer();
        
        // Load initial data
        loadDashboardData();
        
        // Set up button states
        updateButtonStates();
    }
    
    private void startTimer() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    currentTimeLabel.setText(now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")));
                    
                    if (isWorking && punchInTime != null) {
                        long minutes = java.time.Duration.between(punchInTime, now).toMinutes();
                        long hours = minutes / 60;
                        long remainingMinutes = minutes % 60;
                        statusLabel.setText(String.format("Working (%02d:%02d)", hours, remainingMinutes));
                    }
                });
            }
        }, 0, 1000);
    }
    
    private void loadDashboardData() {
        new Thread(() -> {
            try {
                String token = authService.getAuthToken();
                String response = apiService.getDashboardStats(token);
                JsonNode stats = objectMapper.readTree(response);
                
                Platform.runLater(() -> {
                    updateDashboardStats(stats);
                });
                
            } catch (Exception e) {
                logger.error("Failed to load dashboard data", e);
                Platform.runLater(() -> {
                    showError("Failed to load dashboard data");
                });
            }
        }).start();
    }
    
    private void updateDashboardStats(JsonNode stats) {
        if (stats.has("totalHoursThisWeek")) {
            weekHoursLabel.setText(String.format("%.1fh", stats.get("totalHoursThisWeek").asDouble()));
        }
        
        if (stats.has("overtimeHours")) {
            overtimeLabel.setText(String.format("%.1fh", stats.get("overtimeHours").asDouble()));
        }
        
        if (stats.has("activeProjects")) {
            activeProjectsLabel.setText(String.valueOf(stats.get("activeProjects").asInt()));
        }
        
        if (stats.has("overdueTasks")) {
            pendingTasksLabel.setText(String.valueOf(stats.get("overdueTasks").asInt()));
        }
        
        // Add some sample activities
        ObservableList<String> activities = activityListView.getItems();
        activities.clear();
        activities.add("Logged in at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        activities.add("Dashboard loaded successfully");
    }
    
    @FXML
    private void handlePunchIn() {
        setLoading(true);
        
        new Thread(() -> {
            try {
                String token = authService.getAuthToken();
                String notes = notesTextArea.getText().trim();
                String response = apiService.punchIn(token, null, null, notes.isEmpty() ? null : notes);
                
                Platform.runLater(() -> {
                    isWorking = true;
                    punchInTime = LocalDateTime.now();
                    statusLabel.setText("Working (00:00)");
                    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
                    updateButtonStates();
                    notesTextArea.clear();
                    setLoading(false);
                    
                    // Add to activity list
                    activityListView.getItems().add(0, "Punched in at " + 
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                });
                
            } catch (Exception e) {
                logger.error("Punch in failed", e);
                Platform.runLater(() -> {
                    showError("Failed to punch in. Please try again.");
                    setLoading(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handlePunchOut() {
        setLoading(true);
        
        new Thread(() -> {
            try {
                String token = authService.getAuthToken();
                String notes = notesTextArea.getText().trim();
                String response = apiService.punchOut(token, notes.isEmpty() ? null : notes);
                
                Platform.runLater(() -> {
                    isWorking = false;
                    punchInTime = null;
                    statusLabel.setText("Not Working");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
                    updateButtonStates();
                    notesTextArea.clear();
                    setLoading(false);
                    
                    // Add to activity list
                    activityListView.getItems().add(0, "Punched out at " + 
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                    
                    // Reload dashboard data
                    loadDashboardData();
                });
                
            } catch (Exception e) {
                logger.error("Punch out failed", e);
                Platform.runLater(() -> {
                    showError("Failed to punch out. Please try again.");
                    setLoading(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleBreak() {
        showInfo("Break functionality will be implemented in the next version.");
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Any unsaved work will be lost.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                logout();
            }
        });
    }
    
    private void logout() {
        new Thread(() -> {
            try {
                String token = authService.getAuthToken();
                apiService.logout(token);
            } catch (Exception e) {
                logger.error("Logout API call failed", e);
            } finally {
                Platform.runLater(() -> {
                    authService.logout();
                    if (timer != null) {
                        timer.cancel();
                    }
                    switchToLogin();
                });
            }
        }).start();
    }
    
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Team Logger - Login");
            stage.setResizable(false);
            stage.setMaximized(false);
            
        } catch (IOException e) {
            logger.error("Failed to switch to login", e);
            Platform.exit();
        }
    }
    
    private void updateButtonStates() {
        punchInButton.setDisable(isWorking);
        punchOutButton.setDisable(!isWorking);
        breakButton.setDisable(!isWorking);
    }
    
    private void setLoading(boolean loading) {
        punchInButton.setDisable(loading || isWorking);
        punchOutButton.setDisable(loading || !isWorking);
        breakButton.setDisable(loading || !isWorking);
        notesTextArea.setDisable(loading);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Navigation methods (placeholder implementations)
    @FXML
    private void showDashboard() {
        pageTitleLabel.setText("Dashboard");
    }
    
    @FXML
    private void showTimeTracking() {
        pageTitleLabel.setText("Time Tracking");
        showInfo("Time tracking detailed view will be implemented soon.");
    }
    
    @FXML
    private void showProjects() {
        pageTitleLabel.setText("Projects");
        showInfo("Projects view will be implemented soon.");
    }
    
    @FXML
    private void showTasks() {
        pageTitleLabel.setText("Tasks");
        showInfo("Tasks view will be implemented soon.");
    }
    
    @FXML
    private void showReports() {
        pageTitleLabel.setText("Reports");
        showInfo("Reports view will be implemented soon.");
    }
    
    @FXML
    private void showUsers() {
        pageTitleLabel.setText("Users");
        showInfo("Users management will be implemented soon.");
    }
    
    @FXML
    private void showSettings() {
        pageTitleLabel.setText("Settings");
        showInfo("Settings will be implemented soon.");
    }
} 