package com.teamlogger.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class TeamLoggerApp extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(TeamLoggerApp.class);
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            // Create scene with modern styling
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Configure stage
            primaryStage.setTitle("Team Logger - Login");
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED); // Custom window decoration
            primaryStage.setResizable(false);
            
            // Add window dragging functionality
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });
            
            // Set application icon
            try {
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png")));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                logger.warn("Could not load application icon", e);
            }
            
            // Show the stage
            primaryStage.show();
            
            logger.info("Team Logger application started successfully");
            
        } catch (IOException e) {
            logger.error("Failed to start application", e);
            Platform.exit();
        }
    }
    
    @Override
    public void stop() {
        logger.info("Team Logger application shutting down");
        Platform.exit();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 