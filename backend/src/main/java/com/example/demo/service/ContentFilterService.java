package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ContentFilterService {
    
    private final Set<String> forbiddenWords;
    private final Set<String> sensitiveWords;
    private final Pattern urlPattern;
    private final Pattern emailPattern;
    
    public ContentFilterService() {
        // 初始化禁用詞彙
        this.forbiddenWords = new HashSet<>(Arrays.asList(
            "fuck", "shit", "damn", "hell", "bitch", "ass", "crap",
            "幹", "靠", "操", "媽的", "白痴", "智障", "垃圾", "爛"
        ));
        
        // 初始化敏感詞彙
        this.sensitiveWords = new HashSet<>(Arrays.asList(
            "死", "殺", "自殺", "毒品", "酒精", "藥物", "政治", "宗教"
        ));
        
        // URL 和 Email 模式
        this.urlPattern = Pattern.compile("https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+");
        this.emailPattern = Pattern.compile("[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}");
    }
    
    public ContentFilterResult filterContent(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ContentFilterResult(false, "輸入內容不能為空", input);
        }
        
        String cleanedInput = input.trim();
        
        // 檢查長度
        if (cleanedInput.length() > 1000) {
            return new ContentFilterResult(false, "輸入內容過長", cleanedInput);
        }
        
        // 檢查禁用詞彙
        String lowerInput = cleanedInput.toLowerCase();
        for (String forbiddenWord : forbiddenWords) {
            if (lowerInput.contains(forbiddenWord.toLowerCase())) {
                return new ContentFilterResult(false, "輸入包含禁用詞彙", cleanedInput);
            }
        }
        
        // 檢查敏感詞彙
        for (String sensitiveWord : sensitiveWords) {
            if (lowerInput.contains(sensitiveWord.toLowerCase())) {
                return new ContentFilterResult(false, "輸入包含敏感內容，請重新輸入", cleanedInput);
            }
        }
        
        // 移除 URL 和 Email
        cleanedInput = urlPattern.matcher(cleanedInput).replaceAll("[已移除超連結]");
        cleanedInput = emailPattern.matcher(cleanedInput).replaceAll("[已移除電子信箱]");
        
        // 檢查是否與飲品相關
        if (!isDrinkRelated(cleanedInput)) {
            return new ContentFilterResult(false, 
                "請輸入與飲品、心情或口味相關的內容", cleanedInput);
        }
        
        return new ContentFilterResult(true, "內容通過檢查", cleanedInput);
    }
    
    private boolean isDrinkRelated(String input) {
        Set<String> drinkKeywords = new HashSet<>(Arrays.asList(
            // 飲品類型
            "咖啡", "茶", "奶茶", "果汁", "汽水", "酒", "水", "飲料", "飲品",
            "coffee", "tea", "juice", "soda", "drink", "beverage",
            
            // 口味和特徵
            "甜", "酸", "苦", "辣", "鹹", "香", "濃", "淡", "熱", "冰", "溫",
            "sweet", "sour", "bitter", "hot", "cold", "warm", "strong", "light",
            
            // 心情相關
            "開心", "難過", "累", "疲憊", "放鬆", "興奮", "冷靜", "煩躁", "壓力",
            "happy", "sad", "tired", "relaxed", "excited", "calm", "stressed",
            
            // 時間和場合
            "早上", "下午", "晚上", "夜晚", "工作", "休息", "聚會", "約會",
            "morning", "afternoon", "evening", "night", "work", "rest", "party",
            
            // 季節和天氣
            "春天", "夏天", "秋天", "冬天", "熱", "冷", "雨天", "晴天",
            "spring", "summer", "autumn", "winter", "hot", "cold", "rainy", "sunny"
        ));
        
        String lowerInput = input.toLowerCase();
        for (String keyword : drinkKeywords) {
            if (lowerInput.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        // 如果輸入很短，也考慮通過
        return input.length() <= 10;
    }
    
    public static class ContentFilterResult {
        private final boolean isValid;
        private final String message;
        private final String cleanedContent;
        
        public ContentFilterResult(boolean isValid, String message, String cleanedContent) {
            this.isValid = isValid;
            this.message = message;
            this.cleanedContent = cleanedContent;
        }
        
        public boolean isValid() { return isValid; }
        public String getMessage() { return message; }
        public String getCleanedContent() { return cleanedContent; }
    }
}
