package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationResponse {
    private String userInput;
    private String moodDetected;
    private String aiResponse;
    private String aiReason; // 新增：AI 推薦理由，去除編號與飲料名稱的描述
    private List<DrinkRecommendation> recommendations;
    private LocalDateTime timestamp;
    private String status;
    private String message;
    private String serviceType; // 新增：使用的服務類型（AWS Bedrock 或本地推薦引擎）
    private boolean isBedrockAvailable; // 新增：Bedrock 是否可用
    
    // Constructors
    public RecommendationResponse() {
        this.timestamp = LocalDateTime.now();
        this.status = "success";
    }
    
    public RecommendationResponse(String userInput, String moodDetected, String aiResponse, 
                                List<DrinkRecommendation> recommendations) {
        this();
        this.userInput = userInput;
        this.moodDetected = moodDetected;
        this.aiResponse = aiResponse;
        this.recommendations = recommendations;
    }
    
    public RecommendationResponse(String userInput, String moodDetected, String aiResponse, String aiReason,
                                List<DrinkRecommendation> recommendations, String serviceType, boolean isBedrockAvailable) {
        this();
        this.userInput = userInput;
        this.moodDetected = moodDetected;
        this.aiResponse = aiResponse;
        this.aiReason = aiReason;
        this.recommendations = recommendations;
        this.serviceType = serviceType;
        this.isBedrockAvailable = isBedrockAvailable;
    }
    
    // Static factory methods for error responses
    public static RecommendationResponse error(String message) {
        RecommendationResponse response = new RecommendationResponse();
        response.status = "error";
        response.message = message;
        return response;
    }
    
    // Getters and Setters
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    
    public String getMoodDetected() { return moodDetected; }
    public void setMoodDetected(String moodDetected) { this.moodDetected = moodDetected; }
    
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    
    public String getAiReason() { return aiReason; }
    public void setAiReason(String aiReason) { this.aiReason = aiReason; }
    
    public List<DrinkRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<DrinkRecommendation> recommendations) { this.recommendations = recommendations; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public boolean isBedrockAvailable() { return isBedrockAvailable; }
    public void setBedrockAvailable(boolean bedrockAvailable) { this.isBedrockAvailable = bedrockAvailable; }
    
    // Inner class for drink recommendations
    public static class DrinkRecommendation {
        private String name;
        private String category;
        private String description;
        private Double price;
        private String reason;
        private Double matchScore;
        
        public DrinkRecommendation() {}
        
        public DrinkRecommendation(String name, String category, String description, 
                                 Double price, String reason, Double matchScore) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.price = price;
            this.reason = reason;
            this.matchScore = matchScore;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public Double getMatchScore() { return matchScore; }
        public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    }
}
