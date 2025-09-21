package com.example.demo.service;

import com.example.demo.dto.RecommendationResponse;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.util.MenuDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIRecommendationService {
    
    @Autowired
    private ContentFilterService contentFilterService;
    
    @Autowired
    private MoodDetectionService moodDetectionService;
    
    @Autowired
    private BedrockAIService bedrockAIService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private RecommendationHistoryRepository recommendationHistoryRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public RecommendationResponse generateRecommendation(String userInput, String username) {
        try {
            // 1. Guardrails 過濾
            ContentFilterService.ContentFilterResult filterResult = 
                contentFilterService.filterContent(userInput);
            
            if (!filterResult.isValid()) {
                return RecommendationResponse.error(filterResult.getMessage());
            }
            
            String cleanedInput = filterResult.getCleanedContent();
            
            // 2. 心情判斷
            MoodDetectionService.MoodDetectionResult moodResult = 
                moodDetectionService.detectMood(cleanedInput);
            
            // 3. 獲取使用者偏好
            User user = null;
            String userPreferences = "";
            if (username != null && !username.isEmpty()) {
                user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    userPreferences = buildUserPreferencesString(user);
                }
            }
            
            // 4. 生成推薦 prompt
            String prompt = bedrockAIService.buildRecommendationPrompt(
                cleanedInput, moodResult.getDescription(), userPreferences);
            
            // 5. Bedrock Nova 推薦清單 - 使用新的方法獲取服務類型信息
            BedrockAIService.RecommendationResult aiResult = bedrockAIService.generateRecommendation(prompt);
            String aiResponse = aiResult.getRecommendation();
            
            // 確保 AI 回應不為 null 或空
            if (aiResponse == null || aiResponse.trim().isEmpty()) {
                aiResponse = "AI 正在學習您的偏好，請稍候...";
                System.out.println("警告：AI 回應為空，使用默認回應");
            }
            
            // 確保心情描述不為 null
            String moodDescription = moodResult.getDescription();
            if (moodDescription == null || moodDescription.trim().isEmpty()) {
                moodDescription = "中性心情";
                System.out.println("警告：心情描述為空，使用默認值");
            }
            
            // 6. MySQL 查菜單細節
            List<RecommendationResponse.DrinkRecommendation> recommendations = 
                matchMenuItems(aiResponse, moodResult.getMood());
            
            // 確保推薦列表不為 null
            if (recommendations == null) {
                recommendations = new ArrayList<>();
                System.out.println("警告：推薦列表為 null，初始化為空列表");
            }
            
            // 如果沒有匹配到任何推薦，添加一些默認推薦
            if (recommendations.isEmpty()) {
                recommendations = generateFallbackRecommendations(moodResult.getMood());
                System.out.println("沒有匹配到推薦，使用備用推薦列表");
            }
            
            // 7. 保存推薦歷史
            if (user != null) {
                saveRecommendationHistory(user, cleanedInput, moodDescription, 
                                        prompt, aiResponse, recommendations);
            }
            
            // 8. 處理 AI 回應，提取描述理由
            String aiReason = extractAiReason(aiResponse);
            
            // 9. 回傳 API JSON - 包含服務類型信息和 AI 理由，並記錄所有欄位值
            System.out.println("準備回傳的數據:");
            System.out.println("- 用戶輸入: " + cleanedInput);
            System.out.println("- 心情描述: " + moodDescription);
            System.out.println("- AI 回應: " + (aiResponse != null ? aiResponse.substring(0, Math.min(50, aiResponse.length())) + "..." : "null"));
            System.out.println("- AI 理由: " + aiReason);
            System.out.println("- 推薦數量: " + recommendations.size());
            System.out.println("- 服務類型: " + aiResult.getServiceType());
            System.out.println("- Bedrock 可用: " + aiResult.isBedrockAvailable());
            
            return new RecommendationResponse(cleanedInput, moodDescription, 
                                            aiResponse, aiReason, recommendations, 
                                            aiResult.getServiceType(), aiResult.isBedrockAvailable());
            
        } catch (Exception e) {
            return RecommendationResponse.error("推薦系統發生錯誤：" + e.getMessage());
        }
    }
    
    private String buildUserPreferencesString(User user) {
        List<UserPreference> preferences = userPreferenceRepository.findByUser(user);
        if (preferences.isEmpty()) {
            return "";
        }
        
        Map<String, List<String>> groupedPrefs = preferences.stream()
            .collect(Collectors.groupingBy(
                UserPreference::getPreferenceType,
                Collectors.mapping(UserPreference::getPreferenceValue, Collectors.toList())
            ));
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : groupedPrefs.entrySet()) {
            sb.append(entry.getKey()).append(": ")
              .append(String.join(", ", entry.getValue())).append("; ");
        }
        
        return sb.toString();
    }
    
    private List<RecommendationResponse.DrinkRecommendation> matchMenuItems(String aiResponse, String mood) {
        List<RecommendationResponse.DrinkRecommendation> recommendations = new ArrayList<>();
        
        System.out.println("開始解析 AI 回應以生成推薦...");
        System.out.println("AI 回應內容: " + aiResponse.substring(0, Math.min(100, aiResponse.length())) + "...");
        
        // 1. 首先嘗試從結構化 AI 回應中提取推薦（優先級最高）
        BedrockAIService.ParsedAIResponse parsedResponse = bedrockAIService.parseStructuredAIResponse(aiResponse);
        List<BedrockAIService.ParsedAIResponse.DrinkRecommendation> structuredRecs = parsedResponse.getRecommendations();
        
        System.out.println("從結構化解析中提取到 " + structuredRecs.size() + " 個推薦");
        
        // 為結構化推薦創建詳細的推薦對象
        for (BedrockAIService.ParsedAIResponse.DrinkRecommendation structuredRec : structuredRecs) {
            String drinkName = structuredRec.getName();
            String aiReason = structuredRec.getReason();
            
            // 尋找最匹配的菜單項目
            String matchedDrink = findBestMatchingDrinkFromMenu(drinkName);
            
            if (matchedDrink != null) {
                String cleanDrinkName = MenuDataUtil.extractDrinkName(matchedDrink);
                String category = getCategoryFromDrinkName(matchedDrink);
                
                // 計算匹配分數（結構化推薦應該有較高的基礎分數）
                double matchScore = 0.85 + (MenuDataUtil.calculateRelevanceScore(matchedDrink, aiResponse, mood) * 0.15);
                
                String enhancedReason = enhanceRecommendationReason(aiReason, cleanDrinkName, mood);
                double price = generateBasicPrice(cleanDrinkName);
                String description = generateDrinkDescription(cleanDrinkName);
                
                recommendations.add(new RecommendationResponse.DrinkRecommendation(
                    cleanDrinkName, category, description, price, enhancedReason, matchScore
                ));
                
                System.out.println("結構化推薦匹配成功: " + drinkName + " -> " + cleanDrinkName + " (分數: " + String.format("%.2f", matchScore) + ")");
            } else {
                System.out.println("結構化推薦未找到匹配: " + drinkName);
            }
        }
        
        // 2. 如果結構化推薦不足，使用改進的智能匹配作為補充
        if (recommendations.size() < 2) {
            System.out.println("結構化推薦不足，使用智能匹配補充...");
            
            List<String> smartRecommendations = MenuDataUtil.generateSmartRecommendations(aiResponse, mood, 5);
            
            for (String fullDrinkName : smartRecommendations) {
                String drinkName = MenuDataUtil.extractDrinkName(fullDrinkName);
                
                // 檢查是否已經在推薦列表中
                boolean alreadyRecommended = recommendations.stream()
                    .anyMatch(rec -> rec.getName().equals(drinkName));
                
                if (!alreadyRecommended) {
                    String category = getCategoryFromDrinkName(fullDrinkName);
                    double relevanceScore = MenuDataUtil.calculateRelevanceScore(fullDrinkName, aiResponse, mood);
                    double matchScore = Math.max(relevanceScore, calculateMatchScoreByMood(fullDrinkName, mood));
                    
                    String reason = generateAdvancedRecommendationReason(drinkName, mood, aiResponse, relevanceScore);
                    double price = generateBasicPrice(drinkName);
                    String description = generateDrinkDescription(drinkName);
                    
                    recommendations.add(new RecommendationResponse.DrinkRecommendation(
                        drinkName, category, description, price, reason, matchScore
                    ));
                    
                    System.out.println("智能匹配補充: " + drinkName + " (分數: " + String.format("%.2f", matchScore) + ")");
                }
            }
        }
        
        // 3. 如果還是不足，使用備用推薦
        if (recommendations.isEmpty()) {
            System.out.println("沒有找到合適的推薦，使用備用推薦列表");
            recommendations = generateFallbackRecommendations(mood);
        }
        
        // 按匹配分數排序
        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        
        // 確保返回 1-3 個推薦
        int finalCount = Math.max(1, Math.min(3, recommendations.size()));
        List<RecommendationResponse.DrinkRecommendation> finalRecommendations = 
            recommendations.stream().limit(finalCount).collect(Collectors.toList());
            
        System.out.println("最終推薦列表包含 " + finalRecommendations.size() + " 個飲品");
        
        return finalRecommendations;
    }
    
    // 新增：在菜單中尋找最佳匹配的飲品
    private String findBestMatchingDrinkFromMenu(String drinkName) {
        List<String> allDrinks = MenuDataUtil.getAllDrinks();
        String lowerDrinkName = drinkName.toLowerCase();
        String bestMatch = null;
        int bestScore = 0;
        
        for (String menuDrink : allDrinks) {
            String menuDrinkName = MenuDataUtil.extractDrinkName(menuDrink).toLowerCase();
            int score = 0;
            
            // 1. 完全匹配
            if (menuDrinkName.equals(lowerDrinkName)) {
                return menuDrink; // 直接返回完全匹配
            }
            
            // 2. 包含匹配
            if (menuDrinkName.contains(lowerDrinkName) || lowerDrinkName.contains(menuDrinkName)) {
                score = Math.min(menuDrinkName.length(), lowerDrinkName.length()) * 10;
            }
            
            // 3. 關鍵字匹配
            String[] drinkKeywords = lowerDrinkName.split("[/、\\s]+");
            String[] menuKeywords = menuDrinkName.split("[/、\\s]+");
            
            for (String drinkKeyword : drinkKeywords) {
                for (String menuKeyword : menuKeywords) {
                    if (!drinkKeyword.trim().isEmpty() && !menuKeyword.trim().isEmpty() && 
                        drinkKeyword.trim().length() > 1 && menuKeyword.trim().length() > 1) {
                        if (drinkKeyword.trim().equals(menuKeyword.trim())) {
                            score += drinkKeyword.length() * 5;
                        } else if (drinkKeyword.trim().contains(menuKeyword.trim()) || 
                                 menuKeyword.trim().contains(drinkKeyword.trim())) {
                            score += Math.min(drinkKeyword.length(), menuKeyword.length()) * 3;
                        }
                    }
                }
            }
            
            if (score > bestScore) {
                bestScore = score;
                bestMatch = menuDrink;
            }
        }
        
        // 只有當匹配分數足夠高時才返回結果
        return bestScore >= 15 ? bestMatch : null;
    }
    
    // 新增：增強推薦理由
    private String enhanceRecommendationReason(String aiReason, String drinkName, String mood) {
        if (aiReason == null || aiReason.trim().isEmpty()) {
            return generateAdvancedRecommendationReason(drinkName, mood, "", 0.5);
        }
        
        // 如果 AI 理由已經很詳細，直接使用
        if (aiReason.length() > 10) {
            return aiReason;
        }
        
        // 否則增強理由
        return aiReason + "，" + generateSimpleReasonByMood(drinkName, mood);
    }
    
    // 新增：根據心情生成簡單理由
    private String generateSimpleReasonByMood(String drinkName, String mood) {
        String lowerMood = mood != null ? mood.toLowerCase() : "";
        String lowerDrinkName = drinkName.toLowerCase();
        
        if (lowerMood.contains("開心") || lowerMood.contains("happy")) {
            return "能讓好心情持續更久";
        } else if (lowerMood.contains("累") || lowerMood.contains("tired")) {
            if (lowerDrinkName.contains("咖啡") || lowerDrinkName.contains("茶")) {
                return "有助於恢復精神活力";
            } else {
                return "能幫助舒緩疲勞";
            }
        } else if (lowerMood.contains("放鬆") || lowerMood.contains("relax")) {
            return "適合放鬆時光享用";
        } else {
            return "符合您當前的需求";
        }
    }
    
    // 新增：生成備用推薦列表
    private List<RecommendationResponse.DrinkRecommendation> generateFallbackRecommendations(String mood) {
        List<RecommendationResponse.DrinkRecommendation> fallbackRecommendations = new ArrayList<>();
        
        // 從實際菜單中根據心情獲取適合的飲品
        List<String> moodBasedDrinks = MenuDataUtil.getDrinksByMood(mood);
        
        // 隨機選擇 1-3 個飲品作為推薦
        Collections.shuffle(moodBasedDrinks);
        int count = Math.max(1, Math.min(3, moodBasedDrinks.size()));

        for (int i = 0; i < count; i++) {
            String drink = moodBasedDrinks.get(i);
            String drinkName = MenuDataUtil.extractDrinkName(drink);
            String category = getCategoryFromDrinkName(drink);
            
            // 根據心情生成推薦理由
            String reason = generateRecommendationReasonByMood(drinkName, mood);
            
            // 生成匹配分數（根據心情和飲品類型）
            double matchScore = calculateMatchScoreByMood(drink, mood);
            
            // 設定基本價格（實際項目中應該從資料庫獲取）
            double price = generateBasicPrice(drinkName);
            
            fallbackRecommendations.add(new RecommendationResponse.DrinkRecommendation(
                drinkName, category, generateDrinkDescription(drinkName),
                price, reason, matchScore
            ));
        }
        
        // 如果沒有找到適合的飲品，提供一些默認推薦
        if (fallbackRecommendations.isEmpty()) {
            List<String> allDrinks = MenuDataUtil.getAllDrinks();
            Collections.shuffle(allDrinks);
            
            for (int i = 0; i < Math.max(1, Math.min(3, allDrinks.size())); i++) {
                String drink = allDrinks.get(i);
                String drinkName = MenuDataUtil.extractDrinkName(drink);
                String category = getCategoryFromDrinkName(drink);
                
                fallbackRecommendations.add(new RecommendationResponse.DrinkRecommendation(
                    drinkName, category, generateDrinkDescription(drinkName),
                    generateBasicPrice(drinkName), "經典選擇，適合任何時候", 0.75
                ));
            }
        }
        
        return fallbackRecommendations;
    }
    
    private String getCategoryFromDrinkName(String fullDrinkName) {
        if (fullDrinkName.contains("【新鮮果汁系列】")) return "新鮮果汁";
        if (fullDrinkName.contains("【冰沙系列】")) return "冰沙";
        if (fullDrinkName.contains("【特調系列】")) return "特調";
        if (fullDrinkName.contains("【香醇鮮奶系列】")) return "香醇鮮奶";
        if (fullDrinkName.contains("【熱飲系列】")) return "熱飲";
        if (fullDrinkName.contains("【茗茶系列】")) return "茗茶";
        if (fullDrinkName.contains("【奶茶系列】")) return "奶茶";
        if (fullDrinkName.contains("【冬瓜系列】")) return "冬瓜";
        if (fullDrinkName.contains("【多多/多酚系列】")) return "多多/多酚";
        return "飲品";
    }
    
    private String generateRecommendationReasonByMood(String drinkName, String mood) {
        if (mood == null) return "經典選擇，適合任何時候";
        
        switch (mood.toLowerCase()) {
            case "happy":
                return "甜美的口感很適合開心的心情！";
            case "tired":
                return "能幫助提神，恢復活力！";
            case "relaxed":
                return "溫和舒緩，適合放鬆時光！";
            case "stressed":
                return "能幫助舒緩壓力，讓心情平靜！";
            default:
                return "經典選擇，口感絕佳！";
        }
    }
    
    private double calculateMatchScoreByMood(String fullDrinkName, String mood) {
        double baseScore = 0.75;
        
        if (mood == null) return baseScore;
        
        String drinkName = MenuDataUtil.extractDrinkName(fullDrinkName).toLowerCase();
        
        switch (mood.toLowerCase()) {
            case "happy":
                if (drinkName.contains("草莓") || drinkName.contains("芒果") || 
                    drinkName.contains("巧克力") || drinkName.contains("奶昔")) {
                    return 0.90;
                }
                break;
            case "tired":
                if (drinkName.contains("咖啡") || drinkName.contains("茶") ||
                    drinkName.contains("拿鐵")) {
                    return 0.85;
                }
                break;
            case "relaxed":
                if (drinkName.contains("熱") || drinkName.contains("溫") ||
                    drinkName.contains("奶")) {
                    return 0.88;
                }
                break;
            case "stressed":
                if (drinkName.contains("茶") || drinkName.contains("蜂蜜") ||
                    drinkName.contains("薑")) {
                    return 0.87;
                }
                break;
        }
        
        return baseScore + Math.random() * 0.1; // 增加一些隨機性
    }
    
    private String generateDrinkDescription(String drinkName) {
        // 根據飲品名稱生成簡單描述
        if (drinkName.contains("茶")) return "香濃茶香，回甘甘甜";
        if (drinkName.contains("咖啡")) return "濃郁咖啡香氣";
        if (drinkName.contains("奶")) return "香醇濃郁的奶香";
        if (drinkName.contains("果汁")) return "新鮮果汁，天然風味";
        if (drinkName.contains("冰沙")) return "清涼冰沙，口感滑順";
        return "經典飲品，口感絕佳";
    }
    
    private double generateBasicPrice(String drinkName) {
        // 根據飲品類型生成基本價格
        if (drinkName.contains("拿鐵") || drinkName.contains("特調")) return 65.0;
        if (drinkName.contains("鮮奶") || drinkName.contains("奶昔")) return 55.0;
        if (drinkName.contains("果汁")) return 45.0;
        if (drinkName.contains("茶")) return 35.0;
        return 40.0;
    }
    
    private void saveRecommendationHistory(User user, String userInput, String moodDetected,
                                         String prompt, String aiResponse, 
                                         List<RecommendationResponse.DrinkRecommendation> recommendations) {
        try {
            String recommendationsJson = objectMapper.writeValueAsString(recommendations);
            
            RecommendationHistory history = new RecommendationHistory(
                user, userInput, moodDetected, prompt, aiResponse, recommendationsJson
            );
            
            recommendationHistoryRepository.save(history);
        } catch (Exception e) {
            // 記錄錯誤但不影響主要流程
            System.err.println("保存推薦歷史失敗: " + e.getMessage());
        }
    }
    
    private String generateAdvancedRecommendationReason(String drinkName, String mood, String aiResponse, double relevanceScore) {
        String lowerDrinkName = drinkName.toLowerCase();
        
        // 基於相關性分數的前綴
        List<String> aiPrefixes = Arrays.asList(
            "AI 智能分析推薦",
            "根據您的需求精選",
            "智能匹配您的偏好",
            "個性化推薦",
            "專為您量身推薦"
        );
        
        // 檢查是否有成分關係匹配
        String ingredientReason = generateIngredientBasedReason(drinkName, aiResponse);
        if (!ingredientReason.isEmpty()) {
            return getRandomFromList(aiPrefixes) + "：" + ingredientReason;
        }
        
        // 基於心情的理由模板
        Map<String, List<String>> moodReasons = new HashMap<>();
        moodReasons.put("happy", Arrays.asList(
            "甜蜜滋味為您的好心情加分",
            "愉悅口感與您的笑容相得益彰",
            "歡樂時光的最佳搭檔",
            "甜美風味讓快樂加倍",
            "幸福感滿溢的絕佳選擇"
        ));
        moodReasons.put("tired", Arrays.asList(
            "提神醒腦，重新注入活力",
            "疲憊時刻的能量補給站",
            "喚醒沉睡的精神力",
            "為疲憊的身心充電",
            "振奮精神的最佳良伴"
        ));
        moodReasons.put("relaxed", Arrays.asList(
            "悠閒時光的溫柔陪伴",
            "放鬆心靈的療癒選擇",
            "慢生活的完美註腳",
            "寧靜午後的理想夥伴",
            "舒緩身心的溫暖擁抱"
        ));
        moodReasons.put("stressed", Arrays.asList(
            "舒緩緊張情緒的良方",
            "壓力釋放的溫柔解藥",
            "平靜心神的天然選擇",
            "緊繃神經的舒壓聖品",
            "重拾內心平靜的秘密武器"
        ));
        
        // 基於飲品類型的特色描述
        Map<String, List<String>> drinkFeatures = new HashMap<>();
        drinkFeatures.put("茶", Arrays.asList(
            "茶香縈繞，回韻悠長",
            "清香淡雅，韻味深長",
            "茶韻濃郁，層次豐富",
            "古樸茶香，沁人心脾",
            "茶葉精華，自然回甘"
        ));
        drinkFeatures.put("咖啡", Arrays.asList(
            "濃郁咖啡香氣撲鼻",
            "醇厚口感，餘韻繞樑",
            "精品咖啡豆的完美呈現",
            "烘焙香氣，濃情蜜意",
            "咖啡因的溫柔喚醒"
        ));
        drinkFeatures.put("果汁", Arrays.asList(
            "新鮮果香，天然純淨",
            "維生素滿滿的健康選擇",
            "果實精華，營養豐富",
            "清新果味，自然甘甜",
            "鮮榨新鮮，活力滿點"
        ));
        drinkFeatures.put("奶", Arrays.asList(
            "絲滑奶香，溫潤如玉",
            "濃郁奶味，香醇可口",
            "奶香四溢，滑順香甜",
            "牛奶精華，營養滿分",
            "綿密口感，幸福滋味"
        ));
        drinkFeatures.put("冰沙", Arrays.asList(
            "冰涼清爽，瞬間降溫",
            "綿密冰沙，口感層次豐富",
            "清涼解渴，夏日救星",
            "冰晶細膩，入口即化",
            "涼爽體驗，暑氣全消"
        ));
        
        // 溫度相關的描述
        Map<String, List<String>> temperatureFeatures = new HashMap<>();
        temperatureFeatures.put("熱", Arrays.asList(
            "溫暖心房的療癒系飲品",
            "熱氣騰騰，暖胃又暖心",
            "溫度剛好，舒適暖身",
            "熱飲的溫柔擁抱",
            "暖流入喉，幸福感升溫"
        ));
        temperatureFeatures.put("冰", Arrays.asList(
            "冰鎮清涼，瞬間解渴",
            "沁涼入心，暑氣盡消",
            "冰爽體驗，清新怡人",
            "涼意襲人，舒爽無比",
            "冰涼滋味，夏日必備"
        ));
        
        // 組合生成推薦理由
        StringBuilder reason = new StringBuilder();
        
        // 添加 AI 推薦前綴（30% 機率）
        if (relevanceScore >= 0.4 && Math.random() < 0.3) {
            reason.append(getRandomFromList(aiPrefixes)).append("，");
        }
        
        // 添加心情相關理由
        if (mood != null && moodReasons.containsKey(mood.toLowerCase())) {
            reason.append(getRandomFromList(moodReasons.get(mood.toLowerCase())));
        } else {
            reason.append("風味獨特，口感絕佳");
        }
        
        // 添加飲品特色描述
        boolean hasFeature = false;
        for (String key : drinkFeatures.keySet()) {
            if (lowerDrinkName.contains(key)) {
                reason.append("，").append(getRandomFromList(drinkFeatures.get(key)));
                hasFeature = true;
                break;
            }
        }
        
        // 添加溫度特色描述
        for (String key : temperatureFeatures.keySet()) {
            if (lowerDrinkName.contains(key) || lowerDrinkName.contains("溫")) {
                if (hasFeature && Math.random() < 0.5) {
                    // 50% 機率添加溫度描述
                    reason.append("，").append(getRandomFromList(temperatureFeatures.get(key)));
                } else if (!hasFeature) {
                    reason.append("，").append(getRandomFromList(temperatureFeatures.get(key)));
                }
                break;
            }
        }
        
        // 如果沒有任何特色描述，添加通用描述
        if (!hasFeature) {
            List<String> genericFeatures = Arrays.asList(
                "口感層次豐富，令人回味",
                "經典配方，時尚口味",
                "精心調製，品質保證",
                "獨特風味，值得品嚐",
                "匠心工藝，完美呈現"
            );
            reason.append("，").append(getRandomFromList(genericFeatures));
        }
        
        reason.append("！");
        return reason.toString();
    }
    
    // 輔助方法：從列表中隨機選擇一個元素
    private String getRandomFromList(List<String> list) {
        if (list.isEmpty()) return "";
        return list.get(new Random().nextInt(list.size()));
    }
    
    // 新增：處理 AI 回應，提取描述部分並去除編號與飲料名稱
    private String extractAiReason(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return "AI 為您精心挑選的飲品推薦";
        }
        
        StringBuilder aiReason = new StringBuilder();
        String[] lines = aiResponse.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            // 跳過空行
            if (line.isEmpty()) {
                continue;
            }
            
            // 跳過純編號行（如：1. 2. 3. 或 1) 2) 3)）
            if (line.matches("^\\d+[.)].?$")) {
                continue;
            }
            
            // 處理包含編號的行，移除編號和飲料名稱
            if (line.matches("^\\d+[.)].*")) {
                // 移除開頭的編號
                line = line.replaceFirst("^\\d+[.)]\\s*", "");
                
                // 移除可能的飲料名稱（通常在冒號或破折號之前）
                if (line.contains("：") || line.contains(":")) {
                    String[] parts = line.split("[：:]", 2);
                    if (parts.length > 1) {
                        line = parts[1].trim();
                    }
                } else if (line.contains(" - ")) {
                    String[] parts = line.split(" - ", 2);
                    if (parts.length > 1) {
                        line = parts[1].trim();
                    }
                }
            }
            
            // 移除常見的飲料名稱模式（如果行開頭是飲料名稱）
            line = line.replaceFirst("^[\\u4e00-\\u9fff\\w\\s/]+[：:]\\s*", "");
            
            // 如果處理後的行不為空且包含有意義的描述文字，添加到結果中
            if (!line.isEmpty() && line.length() > 5 && !line.matches("^[\\d\\s.()]+$")) {
                if (aiReason.length() > 0) {
                    aiReason.append(" ");
                }
                aiReason.append(line);
                
                // 確保句子以標點符號結尾
                if (!line.endsWith("。") && !line.endsWith("！") && !line.endsWith("？") && 
                    !line.endsWith(".") && !line.endsWith("!") && !line.endsWith("?")) {
                    aiReason.append("。");
                }
            }
        }
        
        String result = aiReason.toString().trim();
        
        // 如果沒有提取到有效內容，提供默認描述
        if (result.isEmpty()) {
            result = "AI 根據您的喜好和當前心情，為您精心挑選了最適合的飲品推薦。";
        }
        
        // 限制長度，避免過長的描述
        if (result.length() > 200) {
            result = result.substring(0, 200) + "...";
        }
        
        return result;
    }
    
    // 新增：根據成分關係生成推薦理由
    private String generateIngredientBasedReason(String drinkName, String aiResponse) {
        String lowerResponse = aiResponse.toLowerCase();
        String cleanDrinkName = MenuDataUtil.extractDrinkName(drinkName);
        
        // 檢查是否有成分關係匹配
        if (lowerResponse.contains("巧克力")) {
            if (cleanDrinkName.contains("阿華田")) {
                return "巧克力愛好者的絕佳選擇！阿華田的醇厚風味完美呈現巧克力的香濃，層次豐富的口感讓人回味無窮。";
            }
        }
        
        if (lowerResponse.contains("鮮奶")) {
            if (cleanDrinkName.contains("奶茶")) {
                return "香醇鮮奶的完美體現！奶茶中濃郁的鮮奶香氣與茶香融合，創造出絲滑順口的極致享受。";
            }
        }
        
        if (lowerResponse.contains("阿華田")) {
            if (cleanDrinkName.contains("巧克力")) {
                return "阿華田的精髓所在！濃郁的巧克力風味正是阿華田的經典特色，帶來熟悉而溫暖的回憶。";
            }
        }
        
        // 檢查反向關係
        if (cleanDrinkName.contains("巧克力") && MenuDataUtil.drinkContainsIngredientOrRelated(drinkName, "阿華田")) {
            return "經典巧克力風味，蘊含著阿華田的醇厚香甜，每一口都是濃郁的幸福滋味。";
        }
        
        if (cleanDrinkName.contains("鮮奶") && MenuDataUtil.drinkContainsIngredientOrRelated(drinkName, "奶茶")) {
            return "新鮮香醇的鮮奶系列，擁有奶茶般的溫潤口感，是喜愛奶香者的不二選擇。";
        }
        
        if (cleanDrinkName.contains("阿華田") && MenuDataUtil.drinkContainsIngredientOrRelated(drinkName, "巧克力")) {
            return "阿華田經典風味，富含濃郁的巧克力精華，帶來溫暖而熟悉的美好味覺體驗。";
        }
        
        return "";
    }
}
