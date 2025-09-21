package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserPreference;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceService userPreferenceService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            
            if (username == null || email == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "使用者名稱和郵箱都是必需的"));
            }
            
            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "使用者名稱已存在"));
            }
            
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "郵箱已被使用"));
            }
            
            User user = new User(username, email);
            User savedUser = userRepository.save(user);
            
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "創建使用者失敗：" + e.getMessage()));
        }
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(userOpt.get());
    }
    
    @GetMapping("/{username}/preferences")
    public ResponseEntity<List<UserPreference>> getUserPreferences(@PathVariable String username) {
        List<UserPreference> preferences = userPreferenceService.getUserPreferences(username);
        return ResponseEntity.ok(preferences);
    }
    
    @PostMapping("/{username}/preferences")
    public ResponseEntity<?> updateUserPreferences(
            @PathVariable String username,
            @RequestBody Map<String, String> preferences) {
        
        try {
            userPreferenceService.updateUserPreferences(username, preferences);
            return ResponseEntity.ok(Map.of("message", "偏好設定更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "更新偏好設定失敗：" + e.getMessage()));
        }
    }
    
    @PutMapping("/{username}/preferences/weight")
    public ResponseEntity<?> updatePreferenceWeight(
            @PathVariable String username,
            @RequestParam String type,
            @RequestParam String value,
            @RequestParam Double weight) {
        
        try {
            if (weight < 0.0 || weight > 1.0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "權重值必須在0.0到1.0之間"));
            }
            
            userPreferenceService.updatePreferenceWeight(username, type, value, weight);
            return ResponseEntity.ok(Map.of("message", "偏好權重更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "更新偏好權重失敗：" + e.getMessage()));
        }
    }
}
