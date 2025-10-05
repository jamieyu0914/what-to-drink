package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class BedrockAIService {
    
    // 推薦結果類別，包含推薦內容和服務類型
    public static class RecommendationResult {
        private String recommendation;
        private String serviceType;
        private boolean isBedrockAvailable;
        
        public RecommendationResult(String recommendation, String serviceType, boolean isBedrockAvailable) {
            this.recommendation = recommendation;
            this.serviceType = serviceType;
            this.isBedrockAvailable = isBedrockAvailable;
        }
        
        // Getters
        public String getRecommendation() { return recommendation; }
        public String getServiceType() { return serviceType; }
        public boolean isBedrockAvailable() { return isBedrockAvailable; }
        
        // Setters
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }
        public void setBedrockAvailable(boolean bedrockAvailable) { this.isBedrockAvailable = bedrockAvailable; }
    }
    
    private BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    
    @Value("${aws.access-key-id}")
    private String awsAccessKeyId;
    
    @Value("${aws.secret-access-key}")
    private String awsSecretAccessKey;
    
    @Value("${aws.region}")
    private String awsRegion;
    
    public BedrockAIService() {
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.builder().build();
        this.bedrockClient = null; // 將在 @PostConstruct 中初始化
    }
    
    @PostConstruct
    public void init() {
        System.out.println("正在初始化 BedrockAIService...");
        
        // 延遲初始化，等待 DotenvConfig 載入環境變數
        // 由於 @PostConstruct 的執行順序可能不固定，我們將在第一次使用時初始化
        System.out.println("BedrockAIService 初始化完成，將在首次使用時載入 AWS 配置");
    }
    
    // 延遲初始化 Bedrock 客戶端
    private synchronized void initializeBedrockClient() {
        if (bedrockClient != null) {
            return; // 已經初始化過了
        }
        
        System.out.println("正在初始化 Bedrock 客戶端...");
        System.out.println("AWS Access Key ID: " + (awsAccessKeyId != null ? awsAccessKeyId.substring(0, Math.min(4, awsAccessKeyId.length())) + "..." : "null"));
        System.out.println("AWS Region: " + awsRegion);
        
        // 嘗試初始化 Bedrock 客戶端
        try {
            if (isBedrockConfiguredInternal()) {
                AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);
                this.bedrockClient = BedrockRuntimeClient.builder()
                        .region(Region.of(awsRegion))
                        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                        .build();
                System.out.println("Bedrock 客戶端初始化成功");
            } else {
                System.out.println("AWS 憑證未配置，將使用本地推薦引擎");
                this.bedrockClient = null;
            }
        } catch (Exception e) {
            System.out.println("Bedrock 客戶端初始化失敗: " + e.getMessage());
            this.bedrockClient = null;
        }
        
        System.out.println("當前服務類型: " + getCurrentServiceType());
    }
    
    // 如果沒有 AWS Bedrock，可以使用這個方法作為備用
    public RecommendationResult generateRecommendation(String prompt) {
        try {
            // 確保 Bedrock 客戶端已初始化
            initializeBedrockClient();
            
            // 檢查 AWS 憑證是否已配置和客戶端是否可用
            if (!isBedrockConfigured() || bedrockClient == null) {
                System.out.println("AWS 憑證未配置或客戶端不可用，使用本地推薦邏輯");
                String localRecommendation = generateLocalRecommendation(prompt);
                return new RecommendationResult(localRecommendation, "本地推薦引擎", false);
            }
            String bedrockRecommendation = generateRecommendationWithBedrock(prompt);
            return new RecommendationResult(bedrockRecommendation, "AWS Bedrock", true);
        } catch (Exception e) {
            System.out.println("Bedrock AI 服務調用失敗，使用本地推薦邏輯: " + e.getMessage());
            // 如果 Bedrock 失敗，使用本地邏輯生成推薦
            String localRecommendation = generateLocalRecommendation(prompt);
            return new RecommendationResult(localRecommendation, "本地推薦引擎(Bedrock故障)", false);
        }
    }
    
    // 成本優化設定 - 使用較低的 token 限制來節省費用
    private String generateRecommendationWithBedrock(String prompt) {
        try {
            // 確保客戶端已初始化
            if (bedrockClient == null) {
                throw new RuntimeException("Bedrock 客戶端未初始化");
            }
            
            // 準備請求 - 使用正確的 Nova 模型格式
            Map<String, Object> requestBody = new HashMap<>();
            
            // Nova 模型使用 messages 格式 - content 需要是包含 text 對象的陣列
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("text", prompt);
            
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", new Object[]{textContent});
            
            requestBody.put("messages", new Object[]{message});
            
            // 將推理參數放在 inferenceConfig 區塊中 - 成本優化設定
            Map<String, Object> inferenceConfig = new HashMap<>();
            inferenceConfig.put("maxTokens", 200);   // 大幅降低 token 限制以節省成本
            inferenceConfig.put("temperature", 0.3); // 降低隨機性，提高回應一致性和簡潔性
            inferenceConfig.put("topP", 0.7);        // 降低創造性，生成更簡潔的回應
            
            requestBody.put("inferenceConfig", inferenceConfig);
            
            String jsonString = objectMapper.writeValueAsString(requestBody);
            
            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("amazon.nova-micro-v1:0") // 使用最便宜的 Nova Micro 模型 (純文本，最經濟)
                    .body(SdkBytes.fromUtf8String(jsonString))
                    .contentType("application/json")
                    .accept("application/json")
                    .build();
            
            InvokeModelResponse response = bedrockClient.invokeModel(request);
            String responseBody = response.body().asUtf8String();
            
            // 解析回應 - Nova 模型的回應格式
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            // Nova 模型的標準回應格式
            JsonNode output = jsonResponse.path("output");
            if (output.has("message")) {
                JsonNode content = output.path("message").path("content");
                if (content.isArray() && content.size() > 0) {
                    return content.get(0).path("text").asText();
                }
            }
            
            // 備用解析路徑 - 如果格式不同
            if (jsonResponse.has("completion")) {
                return jsonResponse.path("completion").asText();
            }
            
            // 如果都找不到，嘗試從最外層找 text
            if (jsonResponse.has("text")) {
                return jsonResponse.path("text").asText();
            }
            
            throw new RuntimeException("無法解析 Nova 模型回應，回應格式: " + responseBody);
            
        } catch (Exception e) {
            throw new RuntimeException("Bedrock AI 服務調用失敗: " + e.getMessage(), e);
        }
    }
    
    private String generateLocalRecommendation(String prompt) {
        // 本地推薦邏輯作為備用方案
        if (prompt.toLowerCase().contains("開心") || prompt.toLowerCase().contains("happy")) {
            return "基於您開心的心情，我推薦以下飲品：\n\n" +
                   "1. 🍓 草莓奶昔 - 甜美的草莓味讓好心情加分\n" +
                   "2. ☕ 焦糖瑪奇朵 - 香甜的焦糖香氣帶來溫暖\n" +
                   "3. 🥤 氣泡水果茶 - 清爽的氣泡感增添愉悅感\n\n" +
                   "這些飲品都有甜美的口感，很適合慶祝好心情！";
        } else if (prompt.toLowerCase().contains("累") || prompt.toLowerCase().contains("tired")) {
            return "感覺到您的疲憊，推薦這些能量飲品：\n\n" +
                   "1. ☕ 美式咖啡 - 純粹的咖啡因提神醒腦\n" +
                   "2. 🍵 抹茶拿鐵 - 溫和的咖啡因加上舒緩的抹茶\n" +
                   "3. 🥤 維他命C果汁 - 補充維生素恢復活力\n\n" +
                   "建議選擇含咖啡因的飲品來恢復精神！";
        } else if (prompt.toLowerCase().contains("放鬆") || prompt.toLowerCase().contains("relax")) {
            return "為了幫助您放鬆，推薦這些舒緩飲品：\n\n" +
                   "1. 🍵 洋甘菊茶 - 天然的放鬆草本茶\n" +
                   "2. 🥛 溫牛奶 - 經典的安神飲品\n" +
                   "3. 🍯 蜂蜜檸檬茶 - 溫暖舒緩的口感\n\n" +
                   "這些飲品都有很好的放鬆效果，適合休息時光！";
        } else {
            return "基於您的需求，我推薦以下多樣化的飲品選擇：\n\n" +
                   "1. ☕ 拿鐵咖啡 - 經典的咖啡飲品\n" +
                   "2. 🍵 烏龍茶 - 清香的茶類選擇\n" +
                   "3. 🥤 鮮榨柳橙汁 - 健康的果汁選項\n\n" +
                   "這些都是受歡迎的飲品，相信您會喜歡！";
        }
    }
    
    public String buildRecommendationPrompt(String userInput, String mood, String userPreferences) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("您是一個專業的飲品推薦助手。請按照以下格式推薦2-3種適合的飲品：\n\n");
        prompt.append("您的輸入：").append(userInput).append("\n");
        
        // 改進的心情檢查邏輯 - 排除 null、空字串、"undefined"、"null" 等無效值
        if (isValidMood(mood)) {
            prompt.append("檢測到的心情：").append(mood).append("\n");
        }
        
        if (userPreferences != null && !userPreferences.trim().isEmpty() && !userPreferences.equals("undefined")) {
            prompt.append("您的偏好：").append(userPreferences).append("\n");
        }
        
        prompt.append("\n請按照以下格式回應（繁體中文）：\n");
        prompt.append("總結分析：[簡短分析您的需求]\n\n");
        prompt.append("推薦飲品：\n");
        prompt.append("1. [飲品名稱] - [一句話推薦理由]\n");
        prompt.append("2. [飲品名稱] - [一句話推薦理由]\n");
        prompt.append("3. [飲品名稱] - [一句話推薦理由]\n\n");
        prompt.append("請確保飲品名稱清晰明確，避免過度描述。\n");
        
        return prompt.toString();
    }
    
    // 新增：解析結構化AI回應，提取推薦和理由
    public static class ParsedAIResponse {
        private String analysis;
        private List<DrinkRecommendation> recommendations;
        
        public ParsedAIResponse(String analysis, List<DrinkRecommendation> recommendations) {
            this.analysis = analysis;
            this.recommendations = recommendations;
        }
        
        public String getAnalysis() { return analysis; }
        public List<DrinkRecommendation> getRecommendations() { return recommendations; }
        
        public static class DrinkRecommendation {
            private String name;
            private String reason;
            
            public DrinkRecommendation(String name, String reason) {
                this.name = name;
                this.reason = reason;
            }
            
            public String getName() { return name; }
            public String getReason() { return reason; }
        }
    }
    
    // 新增：解析結構化的AI回應
    public ParsedAIResponse parseStructuredAIResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return new ParsedAIResponse("", new ArrayList<>());
        }
        
        String analysis = "";
        List<ParsedAIResponse.DrinkRecommendation> recommendations = new ArrayList<>();
        
        // 提取總結分析部分
        Pattern analysisPattern = Pattern.compile("總結分析[：:](.*?)(?=推薦飲品|$)", Pattern.DOTALL);
        Matcher analysisMatcher = analysisPattern.matcher(aiResponse);
        if (analysisMatcher.find()) {
            analysis = analysisMatcher.group(1).trim();
        }
        
        // 提取推薦飲品部分 - 支持多種格式
        Pattern recommendationPattern = Pattern.compile(
            "(?:^|\\n)\\s*(?:[0-9]+[.)、]|[一二三四五六七八九十][.)、]|[⭐🍹☕🍵🥤🥛]?)\\s*([^\\n-]+?)\\s*[-–—]\\s*([^\\n]+)", 
            Pattern.MULTILINE
        );
        
        Matcher recMatcher = recommendationPattern.matcher(aiResponse);
        while (recMatcher.find() && recommendations.size() < 5) {
            String drinkName = recMatcher.group(1).trim();
            String reason = recMatcher.group(2).trim();
            
            // 清理飲品名稱
            drinkName = drinkName.replaceAll("^[⭐🍹☕🍵🥤🥛\\s]+", "");
            drinkName = drinkName.replaceAll("[，,。\\s]*$", "");
            
            if (!drinkName.isEmpty() && !reason.isEmpty()) {
                recommendations.add(new ParsedAIResponse.DrinkRecommendation(drinkName, reason));
            }
        }
        
        // 如果沒有找到結構化推薦，嘗試簡單的列表格式
        if (recommendations.isEmpty()) {
            Pattern simplePattern = Pattern.compile(
                "(?:^|\\n)\\s*(?:[0-9]+[.)、]|[⭐🍹☕🍵🥤🥛]?)\\s*([^\\n]+)", 
                Pattern.MULTILINE
            );
            
            Matcher simpleMatcher = simplePattern.matcher(aiResponse);
            while (simpleMatcher.find() && recommendations.size() < 5) {
                String line = simpleMatcher.group(1).trim();
                if (line.length() > 2 && !line.startsWith("基於") && !line.startsWith("推薦")) {
                    String drinkName = line.split("[-–—,，]")[0].trim();
                    String reason = "根據您的需求推薦";
                    
                    if (line.contains("-") || line.contains("–") || line.contains("—")) {
                        String[] parts = line.split("[-–—]", 2);
                        if (parts.length > 1) {
                            drinkName = parts[0].trim();
                            reason = parts[1].trim();
                        }
                    }
                    
                    recommendations.add(new ParsedAIResponse.DrinkRecommendation(drinkName, reason));
                }
            }
        }
        
        return new ParsedAIResponse(analysis, recommendations);
    }
    
    // 新增輔助方法來檢查心情是否有效
    private boolean isValidMood(String mood) {
        if (mood == null) {
            return false;
        }
        
        String trimmedMood = mood.trim().toLowerCase();
        
        // 排除常見的無效值
        return !trimmedMood.isEmpty() && 
               !trimmedMood.equals("undefined") && 
               !trimmedMood.equals("null") && 
               !trimmedMood.equals("none") &&
               !trimmedMood.equals("unknown");
    }
    
    // 檢查 AWS Bedrock 是否可用 (內部使用，避免遞迴)
    private boolean isBedrockConfiguredInternal() {
        return awsAccessKeyId != null && !awsAccessKeyId.trim().isEmpty() && !awsAccessKeyId.equals("your-access-key") && 
               awsSecretAccessKey != null && !awsSecretAccessKey.trim().isEmpty() && !awsSecretAccessKey.equals("your-secret-key") &&
               awsRegion != null && !awsRegion.trim().isEmpty();
    }
    
    // 檢查 AWS Bedrock 是否可用
    public boolean isBedrockConfigured() {
        // 確保初始化
        if (bedrockClient == null) {
            initializeBedrockClient();
        }
        
        return isBedrockConfiguredInternal() && bedrockClient != null;
    }
    
    // 獲取當前使用的服務類型
    public String getCurrentServiceType() {
        return isBedrockConfigured() ? "AWS Bedrock" : "本地推薦引擎";
    }
}
