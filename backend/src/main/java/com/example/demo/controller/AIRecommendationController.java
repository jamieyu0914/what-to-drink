package com.example.demo.controller;

import com.example.demo.dto.RecommendationRequest;
import com.example.demo.dto.RecommendationResponse;
import com.example.demo.service.AIRecommendationService;
import com.example.demo.service.BedrockAIService;
import com.example.demo.service.UserPreferenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class AIRecommendationController {
    
    @Autowired
    private AIRecommendationService aiRecommendationService;
    
    @Autowired
    private UserPreferenceService userPreferenceService;
    
    @Autowired
    private BedrockAIService bedrockAIService;
    
    @PostMapping("/generate")
    public ResponseEntity<RecommendationResponse> generateRecommendation(
            @Valid @RequestBody RecommendationRequest request) {
        
        try {
            RecommendationResponse response = aiRecommendationService.generateRecommendation(
                request.getUserInput(), request.getUsername());
            
            // 分析並更新使用者偏好（如果有使用者名稱）
            System.out.println("Request Username: " + request.getUsername());
            System.out.println("Response Recommendations: " + response.getRecommendations());
            if (request.getUsername() != null && !request.getUsername().isEmpty() && 
                response.getRecommendations() != null) {
                
                List<String> recommendedDrinkNames = response.getRecommendations().stream()
                    .map(RecommendationResponse.DrinkRecommendation::getName)
                    .toList();
                
                userPreferenceService.analyzeAndUpdatePreferences(
                    request.getUsername(), request.getUserInput(), recommendedDrinkNames);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(RecommendationResponse.error("推薦生成失敗：" + e.getMessage()));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("AI Recommendation API is working!");
    }
    
    @GetMapping("/service-status")
    public ResponseEntity<Map<String, Object>> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("serviceType", bedrockAIService.getCurrentServiceType());
        status.put("isBedrockAvailable", bedrockAIService.isBedrockConfigured());
        status.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(status);
    }
}