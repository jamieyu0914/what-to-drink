package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_drinks")
public class FavoriteDrink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "drink_name", nullable = false)
    private String drinkName;
    
    @Column(name = "drink_category")
    private String drinkCategory;
    
    @Column(name = "drink_description", columnDefinition = "TEXT")
    private String drinkDescription;
    
    @Column(name = "price")
    private Double price;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON format for tags
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // User's personal notes
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public FavoriteDrink() {
        this.createdAt = LocalDateTime.now();
    }
    
    public FavoriteDrink(User user, String drinkName, String drinkCategory) {
        this();
        this.user = user;
        this.drinkName = drinkName;
        this.drinkCategory = drinkCategory;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getDrinkName() { return drinkName; }
    public void setDrinkName(String drinkName) { this.drinkName = drinkName; }
    
    public String getDrinkCategory() { return drinkCategory; }
    public void setDrinkCategory(String drinkCategory) { this.drinkCategory = drinkCategory; }
    
    public String getDrinkDescription() { return drinkDescription; }
    public void setDrinkDescription(String drinkDescription) { this.drinkDescription = drinkDescription; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
