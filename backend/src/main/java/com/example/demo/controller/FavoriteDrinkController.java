package com.example.demo.controller;

import com.example.demo.dto.FavoriteDrinkRequest;
import com.example.demo.model.FavoriteDrink;
import com.example.demo.service.FavoriteDrinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteDrinkController {
    
    @Autowired
    private FavoriteDrinkService favoriteDrinkService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteDrink(
            @RequestParam String username,
            @Valid @RequestBody FavoriteDrinkRequest request) {
        
        try {
            FavoriteDrink favoriteDrink = favoriteDrinkService.addFavoriteDrink(username, request);
            return ResponseEntity.ok(favoriteDrink);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFavoriteDrink(
            @RequestParam String username,
            @RequestParam String drinkName) {
        
        try {
            favoriteDrinkService.removeFavoriteDrink(username, drinkName);
            return ResponseEntity.ok(Map.of("message", "成功移除收藏"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<FavoriteDrink>> getFavoriteDrinks(
            @RequestParam String username,
            @RequestParam(required = false) String category) {
        
        try {
            List<FavoriteDrink> favorites;
            if (category != null && !category.isEmpty()) {
                favorites = favoriteDrinkService.getUserFavoriteDrinksByCategory(username, category);
            } else {
                favorites = favoriteDrinkService.getUserFavoriteDrinks(username);
            }
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @RequestParam String username,
            @RequestParam String drinkName) {
        
        boolean isFavorite = favoriteDrinkService.isFavoriteDrink(username, drinkName);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getFavoriteCount(
            @RequestParam String username) {
        
        long count = favoriteDrinkService.getFavoriteDrinkCount(username);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @PutMapping("/notes")
    public ResponseEntity<?> updateNotes(
            @RequestParam String username,
            @RequestParam String drinkName,
            @RequestBody Map<String, String> request) {
        
        try {
            String notes = request.get("notes");
            FavoriteDrink updated = favoriteDrinkService.updateFavoriteDrinkNotes(username, drinkName, notes);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
