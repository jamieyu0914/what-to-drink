package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserPreference;
import com.example.demo.repository.UserPreferenceRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserPreferenceService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    public void updateUserPreferences(String username, Map<String, String> preferences) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                String type = entry.getKey();
                String value = entry.getValue();
                
                Optional<UserPreference> existingPref = 
                    userPreferenceRepository.findByUserAndPreferenceTypeAndPreferenceValue(user, type, value);
                
                if (existingPref.isEmpty()) {
                    UserPreference newPref = new UserPreference(user, type, value);
                    userPreferenceRepository.save(newPref);
                }
            }
        }
    }
    
    public void updatePreferenceWeight(String username, String type, String value, Double weight) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            Optional<UserPreference> prefOpt = 
                userPreferenceRepository.findByUserAndPreferenceTypeAndPreferenceValue(user, type, value);
            
            if (prefOpt.isPresent()) {
                UserPreference pref = prefOpt.get();
                pref.setWeight(weight);
                userPreferenceRepository.save(pref);
            }
        }
    }
    
    public List<UserPreference> getUserPreferences(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userPreferenceRepository.findByUser(userOpt.get());
        }
        return List.of();
    }
    
    public void analyzeAndUpdatePreferences(String username, String userInput, List<String> recommendedDrinks) {
        // 從使用者輸入分析偏好
        analyzePreferencesFromInput(username, userInput);
        
        // 從推薦結果分析偏好
        analyzePreferencesFromRecommendations(username, recommendedDrinks);
    }
    
    private void analyzePreferencesFromInput(String username, String userInput) {
        String lowerInput = userInput.toLowerCase();
        
        // 溫度偏好
        if (lowerInput.contains("熱") || lowerInput.contains("溫") || lowerInput.contains("hot")) {
            updateUserPreferences(username, Map.of("temperature", "hot"));
        } else if (lowerInput.contains("冰") || lowerInput.contains("冷") || lowerInput.contains("cold")) {
            updateUserPreferences(username, Map.of("temperature", "cold"));
        }
        
        // 甜度偏好
        if (lowerInput.contains("甜") || lowerInput.contains("sweet")) {
            updateUserPreferences(username, Map.of("sweetness", "high"));
        } else if (lowerInput.contains("不甜") || lowerInput.contains("無糖")) {
            updateUserPreferences(username, Map.of("sweetness", "none"));
        }
        
        // 咖啡因偏好
        if (lowerInput.contains("咖啡") || lowerInput.contains("提神") || lowerInput.contains("coffee")) {
            updateUserPreferences(username, Map.of("caffeine", "high"));
        } else if (lowerInput.contains("無咖啡因") || lowerInput.contains("decaf")) {
            updateUserPreferences(username, Map.of("caffeine", "none"));
        }
        
        // 口味偏好
        if (lowerInput.contains("酸") || lowerInput.contains("檸檬") || lowerInput.contains("柑橘")) {
            updateUserPreferences(username, Map.of("flavor", "sour"));
        } else if (lowerInput.contains("苦") || lowerInput.contains("bitter")) {
            updateUserPreferences(username, Map.of("flavor", "bitter"));
        } else if (lowerInput.contains("果") || lowerInput.contains("fruity")) {
            updateUserPreferences(username, Map.of("flavor", "fruity"));
        }
    }
    
    private void analyzePreferencesFromRecommendations(String username, List<String> recommendedDrinks) {
        // 基於推薦的飲品類型分析偏好
        for (String drink : recommendedDrinks) {
            String lowerDrink = drink.toLowerCase();
            
            if (lowerDrink.contains("咖啡") || lowerDrink.contains("coffee")) {
                updateUserPreferences(username, Map.of("category", "coffee"));
            } else if (lowerDrink.contains("茶") || lowerDrink.contains("tea")) {
                updateUserPreferences(username, Map.of("category", "tea"));
            } else if (lowerDrink.contains("果汁") || lowerDrink.contains("juice")) {
                updateUserPreferences(username, Map.of("category", "juice"));
            }
        }
    }
}
