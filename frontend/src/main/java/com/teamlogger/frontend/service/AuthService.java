package com.teamlogger.frontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private static String authToken;
    private static JsonNode currentUser;
    
    public void setAuthToken(String token) {
        authToken = token;
        logger.info("Authentication token set");
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setCurrentUser(JsonNode user) {
        currentUser = user;
        logger.info("Current user set: {}", getUserName());
    }
    
    public JsonNode getCurrentUser() {
        return currentUser;
    }
    
    public String getUserName() {
        if (currentUser != null && currentUser.has("username")) {
            return currentUser.get("username").asText();
        }
        return "Unknown";
    }
    
    public String getFullName() {
        if (currentUser != null) {
            String firstName = currentUser.has("firstName") ? currentUser.get("firstName").asText() : "";
            String lastName = currentUser.has("lastName") ? currentUser.get("lastName").asText() : "";
            return (firstName + " " + lastName).trim();
        }
        return "Unknown User";
    }
    
    public String getUserRole() {
        if (currentUser != null && currentUser.has("role")) {
            return currentUser.get("role").asText();
        }
        return "USER";
    }
    
    public Long getUserId() {
        if (currentUser != null && currentUser.has("id")) {
            return currentUser.get("id").asLong();
        }
        return null;
    }
    
    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(getUserRole());
    }
    
    public boolean isManager() {
        String role = getUserRole();
        return "ADMIN".equals(role) || "MANAGER".equals(role);
    }
    
    public void logout() {
        authToken = null;
        currentUser = null;
        logger.info("User logged out");
    }
    
    public void clearSession() {
        logout();
    }
} 