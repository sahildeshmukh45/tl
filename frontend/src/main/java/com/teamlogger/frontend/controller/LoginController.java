package com.teamlogger.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamlogger.frontend.service.ApiService;
import com.teamlogger.frontend.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberMeCheckbox;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @FXML
    private Hyperlink forgotPasswordLink;
    
    @FXML
    private Hyperlink registerLink;
    
    @FXML
    private Button closeButton;
    
    private final ApiService apiService = new ApiService();
    private final AuthService authService = new AuthService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @FXML
    public void initialize() {
        // Add enter key support for login
        passwordField.setOnAction(event -> handleLogin());
        
        // Add focus listeners for better UX
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateUsername();
            }
        });
        
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validatePassword();
            }
        });
    }
    
    @FXML
    private void handleLogin() {
        if (!validateForm()) {
            return;
        }
        
        setLoading(true);
        
        // Run login in background thread
        new Thread(() -> {
            try {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                boolean rememberMe = rememberMeCheckbox.isSelected();
                
                // Call login API
                String response = apiService.login(username, password, rememberMe);
                JsonNode jsonResponse = objectMapper.readTree(response);
                
                if (jsonResponse.has("token")) {
                    // Login successful
                    String token = jsonResponse.get("token").asText();
                    JsonNode userNode = jsonResponse.get("user");
                    
                    // Store authentication
                    authService.setAuthToken(token);
                    authService.setCurrentUser(userNode);
                    
                    // Switch to dashboard on JavaFX thread
                    Platform.runLater(() -> {
                        try {
                            switchToDashboard();
                        } catch (IOException e) {
                            logger.error("Failed to switch to dashboard", e);
                            showError("Failed to load dashboard");
                        }
                    });
                    
                } else {
                    // Login failed
                    String errorMessage = jsonResponse.has("error") ? 
                            jsonResponse.get("error").asText() : "Login failed";
                    
                    Platform.runLater(() -> {
                        showError(errorMessage);
                        setLoading(false);
                    });
                }
                
            } catch (Exception e) {
                logger.error("Login error", e);
                Platform.runLater(() -> {
                    showError("Connection error. Please check your internet connection.");
                    setLoading(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleForgotPassword() {
        // Show forgot password dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Enter your email address");
        dialog.setContentText("Email:");
        
        dialog.showAndWait().ifPresent(email -> {
            if (email != null && !email.trim().isEmpty()) {
                handleForgotPasswordRequest(email.trim());
            }
        });
    }
    
    private void handleForgotPasswordRequest(String email) {
        setLoading(true);
        
        new Thread(() -> {
            try {
                String response = apiService.forgotPassword(email);
                JsonNode jsonResponse = objectMapper.readTree(response);
                
                if (jsonResponse.has("message")) {
                    Platform.runLater(() -> {
                        showInfo("Password reset email sent to " + email);
                        setLoading(false);
                    });
                } else {
                    String errorMessage = jsonResponse.has("error") ? 
                            jsonResponse.get("error").asText() : "Failed to send reset email";
                    
                    Platform.runLater(() -> {
                        showError(errorMessage);
                        setLoading(false);
                    });
                }
                
            } catch (Exception e) {
                logger.error("Forgot password error", e);
                Platform.runLater(() -> {
                    showError("Failed to send reset email. Please try again.");
                    setLoading(false);
                });
            }
        }).start();
    }
    
    @FXML
    private void handleRegister() {
        showInfo("Please contact your system administrator to create an account.");
    }
    
    @FXML
    private void handleClose() {
        Platform.exit();
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        if (!validateUsername()) {
            isValid = false;
        }
        
        if (!validatePassword()) {
            isValid = false;
        }
        
        return isValid;
    }
    
    private boolean validateUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            showFieldError(usernameField, "Username is required");
            return false;
        }
        clearFieldError(usernameField);
        return true;
    }
    
    private boolean validatePassword() {
        String password = passwordField.getText();
        if (password.isEmpty()) {
            showFieldError(passwordField, "Password is required");
            return false;
        }
        clearFieldError(passwordField);
        return true;
    }
    
    private void showFieldError(Control field, String message) {
        field.setStyle("-fx-border-color: #dc3545; -fx-border-width: 2px;");
        if (field == usernameField) {
            usernameField.setTooltip(new Tooltip(message));
        } else if (field == passwordField) {
            passwordField.setTooltip(new Tooltip(message));
        }
    }
    
    private void clearFieldError(Control field) {
        field.setStyle("");
        field.setTooltip(null);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        loginButton.setDisable(loading);
        usernameField.setDisable(loading);
        passwordField.setDisable(loading);
        rememberMeCheckbox.setDisable(loading);
        
        if (loading) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
    
    private void switchToDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Team Logger - Dashboard");
        stage.setResizable(true);
        stage.setMaximized(true);
        
        // Add window dragging functionality
        root.setOnMousePressed(event -> {
            if (event.getY() < 50) { // Only allow dragging from top area
                root.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
            }
        });
        
        root.setOnMouseDragged(event -> {
            Object userData = root.getUserData();
            if (userData instanceof double[]) {
                double[] offset = (double[]) userData;
                stage.setX(event.getScreenX() - offset[0]);
                stage.setY(event.getScreenY() - offset[1]);
            }
        });
    }
} 