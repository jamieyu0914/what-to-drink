package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RecommendationRequest {
    @NotBlank(message = "使用者輸入不能為空")
    @Size(max = 1000, message = "輸入內容過長")
    private String userInput;
    
    private String username;
    
    // Constructors
    public RecommendationRequest() {}
    
    public RecommendationRequest(String userInput, String username) {
        this.userInput = userInput;
        this.username = username;
    }
    
    // Getters and Setters
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
