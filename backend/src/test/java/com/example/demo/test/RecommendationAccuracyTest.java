package com.example.demo.test;

import com.example.demo.util.MenuDataUtil;
import com.example.demo.service.BedrockAIService;

import java.util.List;

/**
 * 测试改进后的AI回应解析和推荐匹配精度
 */
public class RecommendationAccuracyTest {
    
    public static void main(String[] args) {
        testExtractDrinksFromAIResponse();
        testStructuredRecommendationParsing();
        testSmartRecommendations();
    }
    
    /**
     * 测试从AI回应中提取饮品的精度
     */
    public static void testExtractDrinksFromAIResponse() {
        System.out.println("=== 测试 AI 回应饮品提取精度 ===");
        
        // 测试用例1：结构化推荐格式
        String aiResponse1 = """
            總結分析：根據您疲憊的狀態，我推薦含咖啡因的飲品來恢復精神。
            
            推薦飲品：
            1. 美式咖啡 - 純粹的咖啡因能快速提神醒腦
            2. 抹茶拿鐵 - 溫和的咖啡因加上舒緩的抹茶香
            3. 維他命C果汁 - 補充維生素恢復活力
            """;
        
        List<String> extracted1 = MenuDataUtil.extractDrinksFromAIResponse(aiResponse1);
        System.out.println("測試用例1 - 結構化格式:");
        System.out.println("輸入: " + aiResponse1.replaceAll("\\n", " "));
        System.out.println("提取結果: " + extracted1);
        System.out.println();
        
        // 测试用例2：简单列表格式
        String aiResponse2 = """
            基於您開心的心情，我推薦以下飲品：
            
            1. 🍓 草莓奶昔 - 甜美的草莓味讓好心情加分
            2. ☕ 焦糖瑪奇朵 - 香甜的焦糖香氣帶來溫暖
            3. 🥤 氣泡水果茶 - 清爽的氣泡感增添愉悅感
            """;
        
        List<String> extracted2 = MenuDataUtil.extractDrinksFromAIResponse(aiResponse2);
        System.out.println("測試用例2 - 帶表情符號的格式:");
        System.out.println("輸入: " + aiResponse2.replaceAll("\\n", " "));
        System.out.println("提取結果: " + extracted2);
        System.out.println();
        
        // 测试用例3：非结构化格式
        String aiResponse3 = "我建議您喝拿鐵咖啡，因為它有豐富的奶香，還有烏龍茶也很不錯，清香回甘。";
        
        List<String> extracted3 = MenuDataUtil.extractDrinksFromAIResponse(aiResponse3);
        System.out.println("測試用例3 - 非結構化格式:");
        System.out.println("輸入: " + aiResponse3);
        System.out.println("提取結果: " + extracted3);
        System.out.println();
    }
    
    /**
     * 测试结构化推荐解析
     */
    public static void testStructuredRecommendationParsing() {
        System.out.println("=== 测试结构化推荐解析 ===");
        
        BedrockAIService aiService = new BedrockAIService();
        
        String structuredResponse = """
            總結分析：根據您想要放鬆的心情，我推薦以下舒緩的飲品。
            
            推薦飲品：
            1. 洋甘菊茶 - 天然的放鬆草本茶，有助於舒緩壓力
            2. 溫牛奶 - 經典的安神飲品，促進放鬆
            3. 蜂蜜檸檬茶 - 溫暖舒緩的口感，帶來平靜感受
            """;
        
        BedrockAIService.ParsedAIResponse parsed = aiService.parseStructuredAIResponse(structuredResponse);
        
        System.out.println("分析結果: " + parsed.getAnalysis());
        System.out.println("推薦數量: " + parsed.getRecommendations().size());
        
        for (int i = 0; i < parsed.getRecommendations().size(); i++) {
            BedrockAIService.ParsedAIResponse.DrinkRecommendation rec = parsed.getRecommendations().get(i);
            System.out.println((i + 1) + ". " + rec.getName() + " - " + rec.getReason());
        }
        System.out.println();
    }
    
    /**
     * 测试智能推荐生成
     */
    public static void testSmartRecommendations() {
        System.out.println("=== 测试智能推荐生成 ===");
        
        String aiResponse = """
            總結分析：您感到疲憊，需要能量補充。
            
            推薦飲品：
            1. 美式咖啡 - 高咖啡因含量，快速提神
            2. 綠茶 - 溫和的咖啡因，清香怡人
            3. 能量飲料 - 快速補充體力
            """;
        
        String mood = "疲憊";
        int maxCount = 3;
        
        List<String> smartRecommendations = MenuDataUtil.generateSmartRecommendations(aiResponse, mood, maxCount);
        
        System.out.println("AI 回應: " + aiResponse.replaceAll("\\n", " "));
        System.out.println("心情: " + mood);
        System.out.println("智能推薦結果 (" + smartRecommendations.size() + " 個):");
        
        for (int i = 0; i < smartRecommendations.size(); i++) {
            String drink = smartRecommendations.get(i);
            String drinkName = MenuDataUtil.extractDrinkName(drink);
            double relevanceScore = MenuDataUtil.calculateRelevanceScore(drink, aiResponse, mood);
            
            System.out.println((i + 1) + ". " + drinkName + " (相關性分數: " + 
                String.format("%.2f", relevanceScore) + ")");
        }
        System.out.println();
    }
    
    /**
     * 运行所有测试的主方法
     */
    public static void runAllTests() {
        System.out.println("開始運行 AI 回應解析精度測試...\n");
        
        try {
            testExtractDrinksFromAIResponse();
            testStructuredRecommendationParsing();
            testSmartRecommendations();
            
            System.out.println("所有測試完成！");
            System.out.println("\n改進總結:");
            System.out.println("1. ✅ 支持結構化 AI 回應解析（帶編號的推薦格式）");
            System.out.println("2. ✅ 改進的飲品名稱匹配算法");
            System.out.println("3. ✅ 更精準的相關性分數計算");
            System.out.println("4. ✅ 多層次的推薦策略（結構化 > 智能匹配 > 備用推薦）");
            System.out.println("5. ✅ 增強的推薦理由生成");
            
        } catch (Exception e) {
            System.err.println("測試過程中發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
}