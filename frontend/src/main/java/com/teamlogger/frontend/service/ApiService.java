package com.teamlogger.frontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private static final String BASE_URL = "http://localhost:8080/api";
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    public ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        this.objectMapper = new ObjectMapper();
    }
    
    public String login(String username, String password, boolean rememberMe) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(new LoginRequest(username, password, rememberMe));
        
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/login")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    public String forgotPassword(String email) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(new ForgotPasswordRequest(email));
        
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/forgot-password")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    public String getDashboardStats(String token) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/dashboard/stats")
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    public String punchIn(String token, Long projectId, Long taskId, String notes) throws IOException {
        String url = BASE_URL + "/time-tracking/punch-in";
        if (projectId != null) {
            url += "?projectId=" + projectId;
        }
        if (taskId != null) {
            url += (url.contains("?") ? "&" : "?") + "taskId=" + taskId;
        }
        if (notes != null && !notes.trim().isEmpty()) {
            url += (url.contains("?") ? "&" : "?") + "notes=" + notes;
        }
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("", MediaType.get("application/json")))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    public String punchOut(String token, String notes) throws IOException {
        String url = BASE_URL + "/time-tracking/punch-out";
        if (notes != null && !notes.trim().isEmpty()) {
            url += "?notes=" + notes;
        }
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("", MediaType.get("application/json")))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    public String getCurrentTimeEntry(String token) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/time-tracking/current")
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    public String logout(String token) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/logout")
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("", MediaType.get("application/json")))
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            return response.body().string();
        }
    }
    
    // Helper classes for JSON serialization
    private static class LoginRequest {
        private String username;
        private String password;
        private boolean rememberMe;
        
        public LoginRequest(String username, String password, boolean rememberMe) {
            this.username = username;
            this.password = password;
            this.rememberMe = rememberMe;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public boolean isRememberMe() { return rememberMe; }
        public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
    }
    
    private static class ForgotPasswordRequest {
        private String email;
        
        public ForgotPasswordRequest(String email) {
            this.email = email;
        }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 