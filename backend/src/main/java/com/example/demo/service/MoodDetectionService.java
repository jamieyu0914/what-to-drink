package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class MoodDetectionService {
    
    private final Map<String, Set<String>> moodKeywords = initializeMoodKeywords();
    
    public MoodDetectionService() {
        // Constructor body can be empty now
    }

    private String getMoodDescription(String mood) {
        switch (mood) {
            case "happy": return "開心愉快";
            case "relaxed": return "放鬆平靜";
            case "tired": return "疲憊想休息";
            case "energetic": return "充滿活力";
            case "stressed": return "有壓力焦慮";
            case "sad": return "傷心沮喪";
            case "romantic": return "浪漫溫馨";
            case "focused": return "專注工作";
            case "refreshed": return "需要清爽";
            case "cozy": return "溫暖舒適";
            default: return "中性心情";
        }
    }
    
    private Map<String, Set<String>> initializeMoodKeywords() {
        Map<String, Set<String>> keywords = new HashMap<>();
        
        // 開心/興奮
        keywords.put("happy", new HashSet<>(Arrays.asList(
            "開心", "高興", "快樂", "興奮", "激動", "愉快", "喜悅", "歡樂",
            "happy", "excited", "joyful", "cheerful", "delighted", "elated"
        )));
        
        // 放鬆/平靜
        keywords.put("relaxed", new HashSet<>(Arrays.asList(
            "放鬆", "平靜", "安靜", "冷靜", "舒服", "悠閒", "輕鬆", "寧靜",
            "relaxed", "calm", "peaceful", "tranquil", "serene", "zen"
        )));
        
        // 疲憊/累
        keywords.put("tired", new HashSet<>(Arrays.asList(
            "累", "疲憊", "疲勞", "困", "想睡", "無力", "倦", "乏",
            "tired", "exhausted", "weary", "sleepy", "drained", "fatigue"
        )));
        
        // 有活力/精神
        keywords.put("energetic", new HashSet<>(Arrays.asList(
            "有活力", "精神", "充滿活力", "有精神", "清醒", "振奮", "活躍",
            "energetic", "vigorous", "lively", "active", "alert", "refreshed"
        )));
        
        // 壓力/焦慮
        keywords.put("stressed", new HashSet<>(Arrays.asList(
            "壓力", "焦慮", "緊張", "煩躁", "不安", "憂心", "緊迫", "煩惱",
            "stressed", "anxious", "worried", "nervous", "tense", "overwhelmed"
        )));
        
        // 悲傷/沮喪
        keywords.put("sad", new HashSet<>(Arrays.asList(
            "傷心", "難過", "沮喪", "憂鬱", "低落", "失望", "鬱悶", "悲傷",
            "sad", "depressed", "down", "disappointed", "melancholy", "blue"
        )));
        
        // 浪漫/溫馨
        keywords.put("romantic", new HashSet<>(Arrays.asList(
            "浪漫", "甜蜜", "溫馨", "約會", "情人", "戀愛", "愛情",
            "romantic", "warm", "sweet", "intimate", "loving"
            // "cozy" 已移除，避免與 cozy mood 混淆
        )));
        
        // 專注/工作
        keywords.put("focused", new HashSet<>(Arrays.asList(
            "專注", "工作", "學習", "讀書", "思考", "集中", "努力", "認真",
            "focused", "concentrated", "studying", "working", "productive"
        )));
        
        // 清爽/提神
        keywords.put("refreshed", new HashSet<>(Arrays.asList(
            "清爽", "提神", "refresh", "refreshed", "醒腦", "涼快", "cool", "fresh"
        )));
        
        // 溫暖/舒適
        keywords.put("cozy", new HashSet<>(Arrays.asList(
            "溫暖", "舒適", "cozy", "暖和", "暖", "comfortable", "snug"
        )));
        
        return keywords;
    }
    
    public MoodDetectionResult detectMood(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new MoodDetectionResult("neutral", "無特定心情", 0.0);
        }
        
        String lowerInput = input.toLowerCase();
        Map<String, Integer> moodScores = new HashMap<>();
        
        // 以空白、標點分割詞語
        String[] words = lowerInput.split("[\\s\\p{Punct}]+");
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));
        
        // 計算每種心情的匹配分數（完整詞語比對）
        for (Map.Entry<String, Set<String>> entry : moodKeywords.entrySet()) {
            String mood = entry.getKey();
            Set<String> keywords = entry.getValue();
            
            int score = 0;
            for (String keyword : keywords) {
                String lowerKeyword = keyword.toLowerCase();
                // 完整詞語比對
                if (wordSet.contains(lowerKeyword)) {
                    score += lowerKeyword.length(); // 較長的關鍵字有更高的權重
                }
            }
            
            if (score > 0) {
                moodScores.put(mood, score);
            }
        }
        
        // 找出最高分的心情
        if (moodScores.isEmpty()) {
            return detectMoodFromContext(input);
        }
        
        String detectedMood = Collections.max(moodScores.entrySet(), 
                                            Map.Entry.comparingByValue()).getKey();
        int maxScore = moodScores.get(detectedMood);
        double confidence = Math.min(maxScore / 10.0, 1.0); // 信心度標準化為 0-1
        
        return new MoodDetectionResult(detectedMood, getMoodDescription(detectedMood), confidence);
    }
    
    private MoodDetectionResult detectMoodFromContext(String input) {
        String lowerInput = input.toLowerCase();
        
        // 時間相關的心情推測
        if (lowerInput.contains("早上") || lowerInput.contains("morning")) {
            return new MoodDetectionResult("energetic", "早晨需要活力", 0.6);
        }
        if (lowerInput.contains("下午") || lowerInput.contains("afternoon")) {
            return new MoodDetectionResult("focused", "下午工作時光", 0.5);
        }
        if (lowerInput.contains("晚上") || lowerInput.contains("evening") || 
            lowerInput.contains("夜晚") || lowerInput.contains("night")) {
            return new MoodDetectionResult("relaxed", "夜晚放鬆時刻", 0.6);
        }
        
        // 天氣相關的心情推測
        if (lowerInput.contains("熱") || lowerInput.contains("夏天") || lowerInput.contains("hot")) {
            return new MoodDetectionResult("refreshed", "炎熱天氣", 0.5);
        }
        if (lowerInput.contains("冷") || lowerInput.contains("冬天") || lowerInput.contains("cold")) {
            return new MoodDetectionResult("cozy", "寒冷天氣", 0.5);
        }
        
        // 默認中性心情
        return new MoodDetectionResult("neutral", "中性心情", 0.3);
    }
    
    public static class MoodDetectionResult {
        private final String mood;
        private final String description;
        private final double confidence;
        
        public MoodDetectionResult(String mood, String description, double confidence) {
            this.mood = mood;
            this.description = description;
            this.confidence = confidence;
        }
        
        public String getMood() { return mood; }
        public String getDescription() { return description; }
        public double getConfidence() { return confidence; }
    }
}