package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_history")
public class RecommendationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "user_input", columnDefinition = "TEXT")
    private String userInput;
    
    @Column(name = "mood_detected")
    private String moodDetected;
    
    @Column(name = "recommendation_prompt", columnDefinition = "TEXT")
    private String recommendationPrompt;
    
    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;
    
    @Column(name = "recommended_drinks", columnDefinition = "TEXT")
    private String recommendedDrinks; // JSON format
    
    @Column(name = "user_rating")
    private Integer userRating; // 1-5 stars
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public RecommendationHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    public RecommendationHistory(User user, String userInput, String moodDetected, 
                               String recommendationPrompt, String aiResponse, String recommendedDrinks) {
        this();
        this.user = user;
        this.userInput = userInput;
        this.moodDetected = moodDetected;
        this.recommendationPrompt = recommendationPrompt;
        this.aiResponse = aiResponse;
        this.recommendedDrinks = recommendedDrinks;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    
    public String getMoodDetected() { return moodDetected; }
    public void setMoodDetected(String moodDetected) { this.moodDetected = moodDetected; }
    
    public String getRecommendationPrompt() { return recommendationPrompt; }
    public void setRecommendationPrompt(String recommendationPrompt) { this.recommendationPrompt = recommendationPrompt; }
    
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    
    public String getRecommendedDrinks() { return recommendedDrinks; }
    public void setRecommendedDrinks(String recommendedDrinks) { this.recommendedDrinks = recommendedDrinks; }
    
    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
