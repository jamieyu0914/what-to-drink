package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private Double price;
    
    @Column
    private String temperature; // hot, cold, room
    
    @Column
    private String sweetness; // none, low, medium, high
    
    @Column
    private String caffeine; // none, low, medium, high
    
    @Column
    private String flavor; // fruity, bitter, sweet, sour, etc.
    
    @Column(columnDefinition = "TEXT")
    private String ingredients;
    
    @Column
    private String season; // spring, summer, autumn, winter, all
    
    @Column
    private String mood; // energetic, relaxing, refreshing, cozy, etc.
    
    @Column
    private Boolean available = true;
    
    // Constructors
    public MenuItem() {}
    
    public MenuItem(String name, String category, String description, Double price) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }
    
    public String getSweetness() { return sweetness; }
    public void setSweetness(String sweetness) { this.sweetness = sweetness; }
    
    public String getCaffeine() { return caffeine; }
    public void setCaffeine(String caffeine) { this.caffeine = caffeine; }
    
    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }
    
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
