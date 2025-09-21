package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class FavoriteDrinkRequest {
    @NotBlank(message = "飲品名稱不能為空")
    private String drinkName;
    
    private String drinkCategory;
    private String drinkDescription;
    private Double price;
    private String tags;
    private String notes;
    
    // Constructors
    public FavoriteDrinkRequest() {}
    
    public FavoriteDrinkRequest(String drinkName, String drinkCategory) {
        this.drinkName = drinkName;
        this.drinkCategory = drinkCategory;
    }
    
    // Getters and Setters
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
}

class RatingRequest {
    @Min(value = 1, message = "評分最少為1星")
    @Max(value = 5, message = "評分最多為5星")
    private Integer rating;
    
    private Long historyId;
    
    // Constructors
    public RatingRequest() {}
    
    public RatingRequest(Integer rating, Long historyId) {
        this.rating = rating;
        this.historyId = historyId;
    }
    
    // Getters and Setters
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
}
