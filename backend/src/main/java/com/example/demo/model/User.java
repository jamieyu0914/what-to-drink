package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPreference> preferences;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecommendationHistory> recommendationHistory;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FavoriteDrink> favoriteDrinks;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String username, String email) {
        this();
        this.username = username;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<UserPreference> getPreferences() { return preferences; }
    public void setPreferences(List<UserPreference> preferences) { this.preferences = preferences; }
    
    public List<RecommendationHistory> getRecommendationHistory() { return recommendationHistory; }
    public void setRecommendationHistory(List<RecommendationHistory> recommendationHistory) { this.recommendationHistory = recommendationHistory; }
    
    public List<FavoriteDrink> getFavoriteDrinks() { return favoriteDrinks; }
    public void setFavoriteDrinks(List<FavoriteDrink> favoriteDrinks) { this.favoriteDrinks = favoriteDrinks; }
}
