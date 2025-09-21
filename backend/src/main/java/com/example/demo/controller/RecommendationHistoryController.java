package com.example.demo.controller;

import com.example.demo.model.RecommendationHistory;
import com.example.demo.model.User;
import com.example.demo.repository.RecommendationHistoryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class RecommendationHistoryController {
    
    @Autowired
    private RecommendationHistoryRepository recommendationHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/list")
    public ResponseEntity<List<RecommendationHistory>> getRecommendationHistory(
            @RequestParam String username,
            @RequestParam(defaultValue = "20") int limit) {
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<RecommendationHistory> history = recommendationHistoryRepository
            .findByUserOrderByCreatedAtDesc(userOpt.get(), PageRequest.of(0, limit));
        
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<RecommendationHistory>> getRecentHistory(
            @RequestParam String username,
            @RequestParam(defaultValue = "7") int days) {
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<RecommendationHistory> history = recommendationHistoryRepository
            .findByUserAndCreatedAtAfterOrderByCreatedAtDesc(userOpt.get(), since);
        
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/rate")
    public ResponseEntity<?> rateRecommendation(
            @RequestParam Long historyId,
            @RequestParam Integer rating) {
        
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "評分必須在1-5之間"));
        }
        
        Optional<RecommendationHistory> historyOpt = recommendationHistoryRepository.findById(historyId);
        if (historyOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "推薦記錄不存在"));
        }
        
        RecommendationHistory history = historyOpt.get();
        history.setUserRating(rating);
        recommendationHistoryRepository.save(history);
        
        return ResponseEntity.ok(Map.of("message", "評分成功"));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@RequestParam String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userOpt.get();
        long totalRecommendations = recommendationHistoryRepository.countByUser(user);
        Double averageRating = recommendationHistoryRepository.getAverageRatingByUser(user);
        
        Map<String, Object> stats = Map.of(
            "totalRecommendations", totalRecommendations,
            "averageRating", averageRating != null ? averageRating : 0.0
        );
        
        return ResponseEntity.ok(stats);
    }
}
