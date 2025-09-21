package com.example.demo.util;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class MenuDataUtil {
    
    // 與 MenuController 完全相同的菜單資料
    public static Map<String, List<String>> getDrinksMenu() {
        Map<String, List<String>> menu = new HashMap<>();
        
        // Fresh juices
        menu.put("fresh juices", Arrays.asList(
            "【新鮮果汁系列】西瓜汁",
            "【新鮮果汁系列】金桔檸檬",
            "【新鮮果汁系列】甘蔗青茶",
            "【新鮮果汁系列】鳳梨青茶",
            "【新鮮果汁系列】檸檬原汁",
            "【新鮮果汁系列】葡萄柚綠茶",
            "【新鮮果汁系列】葡萄柚原汁",
            "【新鮮果汁系列】西瓜牛奶",
            "【新鮮果汁系列】木瓜牛奶",
            "【新鮮果汁系列】熱帶水果青茶"
        ));

        // Smoothies
        menu.put("smoothies", Arrays.asList(
            "【冰沙系列】綠豆沙",
            "【冰沙系列】檸檬冰沙",
            "【冰沙系列】酸梅冰沙",
            "【冰沙系列】草莓冰沙",
            "【冰沙系列】芒果冰沙",
            "【冰沙系列】百香果冰沙",
            "【冰沙系列】巧克力冰沙",
            "【冰沙系列】咖啡冰沙",
            "【冰沙系列】綠豆沙牛奶"
        ));

        // Specials
        menu.put("specials", Arrays.asList(
            "【特調系列】仙草蜜",
            "【特調系列】仙草干茶",
            "【特調系列】檸檬愛玉",
            "【特調系列】冰咖啡",
            "【特調系列】蛋密汁",
            "【特調系列】紫蘇梅綠茶",
            "【特調系列】蜂蜜蘆薈",
            "【特調系列】蜂蜜柚子茶",
            "【特調系列】百香雙響砲"
        ));

        // Mellow fresh milks
        menu.put("mellow fresh milks", Arrays.asList(
            "【香醇鮮奶系列】紅茶拿鐵",
            "【香醇鮮奶系列】抹茶拿鐵",
            "【香醇鮮奶系列】香芋鮮奶",
            "【香醇鮮奶系列】杏仁鮮奶",
            "【香醇鮮奶系列】巧克力鮮奶",
            "【香醇鮮奶系列】阿華田鮮奶"
        ));

        // Hot drinks
        menu.put("hot drinks", Arrays.asList(
            "【熱飲系列】薑母茶",
            "【熱飲系列】桂圓茶",
            "【熱飲系列】沖繩奶茶",
            "【熱飲系列】薑母奶茶",
            "【熱飲系列】桂圓奶茶",
            "【熱飲系列】熱可可",
            "【熱飲系列】桂圓紅棗茶",
            "【熱飲系列】熱金桔檸檬",
            "【熱飲系列】薑汁柚子茶",
            "【熱飲系列】熱帶水果清茶",
            "【熱飲系列】地芋珍珠奶茶"
        ));

        // Fine teas
        menu.put("fine teas", Arrays.asList(
            "【茗茶系列】麥香紅茶",
            "【茗茶系列】茉香綠茶",
            "【茗茶系列】四季青茶",
            "【茗茶系列】炭焙烏龍",
            "【茗茶系列】密香紅/綠茶",
            "【茗茶系列】梅子紅/綠茶",
            "【茗茶系列】薄荷紅/綠茶",
            "【茗茶系列】石榴紅/綠茶",
            "【茗茶系列】檸檬紅/綠茶",
            "【茗茶系列】鮮果百香紅/綠茶"
        ));

        // Milk teas
        menu.put("milk teas", Arrays.asList(
            "【奶茶系列】奶茶",
            "【奶茶系列】茉香奶茶",
            "【奶茶系列】烏龍奶茶",
            "【奶茶系列】珍珠奶茶",
            "【奶茶系列】胚芽奶茶",
            "【奶茶系列】布丁奶茶",
            "【奶茶系列】椰果奶茶",
            "【奶茶系列】黑磚奶茶",
            "【奶茶系列】沖繩奶茶",
            "【奶茶系列】泰式奶茶",
            "【奶茶系列】仙草凍奶茶",
            "【奶茶系列】地芋珍珠奶茶"
        ));

        // Winter melons
        menu.put("winter melons", Arrays.asList(
            "【冬瓜系列】冬瓜茶",
            "【冬瓜系列】冬瓜清茶",
            "【冬瓜系列】冬瓜梅子",
            "【冬瓜系列】冬瓜檸檬",
            "【冬瓜系列】冬瓜仙草",
            "【冬瓜系列】仙橙冬瓜",
            "【冬瓜系列】冬瓜鮮奶"
        ));

        // Yakult and polyphenols
        menu.put("yakult and polyphenols", Arrays.asList(
            "【多多/多酚系列】多多綠茶",
            "【多多/多酚系列】多多檸檬",
            "【多多/多酚系列】多酚綠茶",
            "【多多/多酚系列】多酚檸檬",
            "【多多/多酚系列】多酚鮮奶"
        ));

        return menu;
    }
    
    // 獲取所有飲品清單（扁平化）
    public static List<String> getAllDrinks() {
        List<String> allDrinks = new ArrayList<>();
        getDrinksMenu().values().forEach(allDrinks::addAll);
        return allDrinks;
    }
    
    // 根據心情推薦適合的飲品分類
    public static List<String> getDrinksByMood(String mood) {
        Map<String, List<String>> drinksMenu = getDrinksMenu();
        List<String> recommendations = new ArrayList<>();
        
        if (mood == null) {
            return getAllDrinks();
        }
        
        switch (mood.toLowerCase()) {
            case "happy":
                // 開心時推薦甜品和氣泡飲
                recommendations.addAll(drinksMenu.get("smoothies"));
                recommendations.addAll(drinksMenu.get("mellow fresh milks"));
                recommendations.addAll(drinksMenu.get("specials"));
                break;
                
            case "tired":
                // 疲憊時推薦咖啡和茶類
                recommendations.addAll(drinksMenu.get("hot drinks"));
                recommendations.addAll(drinksMenu.get("fine teas"));
                recommendations.addAll(drinksMenu.get("milk teas"));
                break;
                
            case "relaxed":
                // 放鬆時推薦溫和的飲品
                recommendations.addAll(drinksMenu.get("hot drinks"));
                recommendations.addAll(drinksMenu.get("mellow fresh milks"));
                recommendations.addAll(drinksMenu.get("winter melons"));
                break;
                
            case "stressed":
                // 壓力大時推薦舒緩的飲品
                recommendations.addAll(drinksMenu.get("fine teas"));
                recommendations.addAll(drinksMenu.get("specials"));
                recommendations.addAll(drinksMenu.get("hot drinks"));
                break;
                
            default:
                // 中性心情推薦各種飲品
                recommendations.addAll(drinksMenu.get("fresh juices"));
                recommendations.addAll(drinksMenu.get("fine teas"));
                recommendations.addAll(drinksMenu.get("milk teas"));
                break;
        }
        
        return recommendations;
    }
    
    // 提取飲品名稱中的關鍵字（去掉系列標籤）
    public static String extractDrinkName(String fullDrinkName) {
        if (fullDrinkName.contains("】")) {
            return fullDrinkName.substring(fullDrinkName.indexOf("】") + 1);
        }
        return fullDrinkName;
    }
    
    // 根據關鍵字匹配飲品
    public static List<String> findMatchingDrinks(String keyword) {
        List<String> allDrinks = getAllDrinks();
        List<String> matchingDrinks = new ArrayList<>();
        
        String lowerKeyword = keyword.toLowerCase();
        
        for (String drink : allDrinks) {
            String drinkName = extractDrinkName(drink).toLowerCase();
            if (drinkName.contains(lowerKeyword) || lowerKeyword.contains(drinkName)) {
                matchingDrinks.add(drink);
            }
        }
        
        return matchingDrinks;
    }
    
    // 改進：根據 AI 回應內容智能匹配飲品 - 支持結構化推薦格式
    public static List<String> extractDrinksFromAIResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<String> matchedDrinks = new LinkedHashSet<>(); // 使用 LinkedHashSet 保持順序
        List<String> allDrinks = getAllDrinks();
        
        // 1. 首先嘗試解析結構化推薦格式（帶編號的推薦列表）
        List<String> structuredRecommendations = extractStructuredRecommendations(aiResponse);
        if (!structuredRecommendations.isEmpty()) {
            for (String recommendation : structuredRecommendations) {
                String matchedDrink = findBestMatchingDrink(recommendation, allDrinks);
                if (matchedDrink != null) {
                    matchedDrinks.add(matchedDrink);
                }
            }
        }
        
        // 2. 如果結構化解析結果不足，進行傳統的關鍵字匹配
        if (matchedDrinks.size() < 2) {
            String lowerResponse = aiResponse.toLowerCase();
            
            for (String drink : allDrinks) {
                String drinkName = extractDrinkName(drink).toLowerCase();
                
                // 完全匹配飲品名稱
                if (lowerResponse.contains(drinkName)) {
                    matchedDrinks.add(drink);
                    continue;
                }
                
                // 關鍵字分割匹配（處理如"紅/綠茶"這樣的名稱）
                String[] keywords = drinkName.split("[/、]");
                for (String keyword : keywords) {
                    keyword = keyword.trim();
                    if (!keyword.isEmpty() && keyword.length() > 1 && lowerResponse.contains(keyword)) {
                        matchedDrinks.add(drink);
                        break;
                    }
                }
            }
        }
        
        // 3. 如果匹配結果仍然不足，進行語義匹配
        if (matchedDrinks.size() < 2) {
            List<String> semanticMatches = findDrinksBySemanticKeywords(aiResponse);
            matchedDrinks.addAll(semanticMatches.stream().limit(3 - matchedDrinks.size()).collect(Collectors.toList()));
        }
        
        return new ArrayList<>(matchedDrinks);
    }
    
    // 新增：提取結構化推薦格式（如：1. xxx, 2. yyy, 3. zzz）
    private static List<String> extractStructuredRecommendations(String aiResponse) {
        List<String> recommendations = new ArrayList<>();
        
        // 匹配帶編號的推薦格式：1. 、2. 、3. 等
        Pattern numberPattern = Pattern.compile("(?:^|\\n)\\s*(?:[0-9]+[.)、]|[一二三四五六七八九十][.)、]|[⭐🍹☕🍵🥤🥛]?\\s*)\\s*([^\\n]+)", Pattern.MULTILINE);
        Matcher matcher = numberPattern.matcher(aiResponse);
        
        while (matcher.find()) {
            String recommendation = matcher.group(1).trim();
            if (!recommendation.isEmpty() && recommendation.length() > 2) {
                // 清理推薦文本，移除多餘的符號和說明
                recommendation = cleanRecommendationText(recommendation);
                if (!recommendation.isEmpty()) {
                    recommendations.add(recommendation);
                }
            }
        }
        
        // 如果沒有找到編號格式，嘗試匹配其他結構化格式
        if (recommendations.isEmpty()) {
            // 匹配直接列出的飲品（換行分隔）
            String[] lines = aiResponse.split("\\n");
            for (String line : lines) {
                line = line.trim();
                if (line.length() > 2 && !line.startsWith("基於") && !line.startsWith("為了") && 
                    !line.startsWith("這些") && !line.contains("推薦") && !line.contains("建議")) {
                    String cleanLine = cleanRecommendationText(line);
                    if (!cleanLine.isEmpty() && containsDrinkKeyword(cleanLine)) {
                        recommendations.add(cleanLine);
                    }
                }
            }
        }
        
        return recommendations.stream().limit(5).collect(Collectors.toList()); // 最多返回5個推薦
    }
    
    // 新增：清理推薦文本
    private static String cleanRecommendationText(String text) {
        if (text == null) return "";
        
        // 移除常見的描述詞和符號
        text = text.replaceAll("^[⭐🍹☕🍵🥤🥛\\s-]+", ""); // 移除開頭的表情符號和符號
        text = text.replaceAll("\\s*[-–—]\\s*.*$", ""); // 移除破折號後的說明部分
        text = text.replaceAll("\\s*[，,]\\s*.*$", ""); // 移除逗號後的說明部分
        text = text.replaceAll("\\s*[(（].*[)）]\\s*", ""); // 移除括號內容
        text = text.replaceAll("^[、。，,\\s]+|[、。，,\\s]+$", ""); // 移除開頭和結尾的標點符號
        
        return text.trim();
    }
    
    // 新增：檢查文本是否包含飲品關鍵字
    private static boolean containsDrinkKeyword(String text) {
        String lowerText = text.toLowerCase();
        String[] drinkKeywords = {"茶", "咖啡", "果汁", "奶", "水", "沙", "可樂", "汽水", "飲", "湯", "酒"};
        
        for (String keyword : drinkKeywords) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    // 新增：在所有飲品中找到最佳匹配的飲品
    private static String findBestMatchingDrink(String recommendation, List<String> allDrinks) {
        String lowerRecommendation = recommendation.toLowerCase();
        String bestMatch = null;
        int bestScore = 0;
        
        for (String drink : allDrinks) {
            String drinkName = extractDrinkName(drink).toLowerCase();
            int score = 0;
            
            // 1. 完全匹配得分最高
            if (lowerRecommendation.contains(drinkName)) {
                score = 100 + drinkName.length(); // 長度越長越精確
            } else {
                // 2. 關鍵字匹配
                String[] drinkKeywords = drinkName.split("[/、\\s]+");
                for (String keyword : drinkKeywords) {
                    keyword = keyword.trim();
                    if (!keyword.isEmpty() && keyword.length() > 1 && lowerRecommendation.contains(keyword)) {
                        score += keyword.length() * 10; // 關鍵字長度 * 10
                    }
                }
            }
            
            if (score > bestScore) {
                bestScore = score;
                bestMatch = drink;
            }
        }
        
        // 只有當匹配分數足夠高時才返回結果
        return bestScore >= 20 ? bestMatch : null;
    }
    
    // 新增：根據語義關鍵字找尋飲品
    public static List<String> findDrinksBySemanticKeywords(String text) {
        Map<String, List<String>> drinksMenu = getDrinksMenu();
        List<String> semanticMatches = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        // 語義關鍵字映射
        Map<String, List<String>> semanticMap = new HashMap<>();
        semanticMap.put("咖啡", Arrays.asList("coffee", "咖啡", "拿鐵", "美式"));
        semanticMap.put("茶", Arrays.asList("tea", "茶", "綠茶", "紅茶", "烏龍", "青茶"));
        semanticMap.put("果汁", Arrays.asList("juice", "果汁", "柳橙", "蘋果", "葡萄", "鳳梨", "西瓜"));
        semanticMap.put("奶茶", Arrays.asList("milk tea", "奶茶", "珍珠", "布丁"));
        semanticMap.put("冰沙", Arrays.asList("smoothie", "冰沙", "芒果", "草莓", "巧克力"));
        semanticMap.put("熱飲", Arrays.asList("hot drink", "熱飲", "薑母", "桂圓", "熱可可"));
        semanticMap.put("甜品", Arrays.asList("sweet", "甜", "巧克力", "草莓", "芒果", "蜂蜜"));
        semanticMap.put("清爽", Arrays.asList("fresh", "清爽", "檸檬", "薄荷", "冬瓜", "綠豆"));
        
        // 檢查語義匹配
        for (Map.Entry<String, List<String>> entry : semanticMap.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();
            
            boolean hasKeyword = keywords.stream().anyMatch(lowerText::contains);
            if (hasKeyword) {
                switch (category) {
                    case "咖啡":
                        semanticMatches.addAll(drinksMenu.get("specials"));
                        break;
                    case "茶":
                        semanticMatches.addAll(drinksMenu.get("fine teas"));
                        break;
                    case "果汁":
                        semanticMatches.addAll(drinksMenu.get("fresh juices"));
                        break;
                    case "奶茶":
                        semanticMatches.addAll(drinksMenu.get("milk teas"));
                        break;
                    case "冰沙":
                        semanticMatches.addAll(drinksMenu.get("smoothies"));
                        break;
                    case "熱飲":
                        semanticMatches.addAll(drinksMenu.get("hot drinks"));
                        break;
                    case "甜品":
                        semanticMatches.addAll(drinksMenu.get("mellow fresh milks"));
                        semanticMatches.addAll(drinksMenu.get("smoothies"));
                        break;
                    case "清爽":
                        semanticMatches.addAll(drinksMenu.get("fresh juices"));
                        semanticMatches.addAll(drinksMenu.get("winter melons"));
                        break;
                }
            }
        }
        
        return semanticMatches.stream().distinct().collect(Collectors.toList());
    }
    
    // 改進：根據 AI 回應和心情生成智能推薦 - 優先使用 AI 回應結構
    public static List<String> generateSmartRecommendations(String aiResponse, String mood, int maxCount) {
        List<String> recommendations = new ArrayList<>();
        
        // 1. 優先從 AI 回應中提取結構化推薦（這是最精準的）
        List<String> aiMentionedDrinks = extractDrinksFromAIResponse(aiResponse);
        
        // 對提取到的飲品進行相關性排序
        List<String> sortedAIDrinks = aiMentionedDrinks.stream()
            .sorted((drink1, drink2) -> {
                double score1 = calculateRelevanceScore(drink1, aiResponse, mood);
                double score2 = calculateRelevanceScore(drink2, aiResponse, mood);
                return Double.compare(score2, score1); // 降序排列
            })
            .collect(Collectors.toList());
            
        recommendations.addAll(sortedAIDrinks);
        
        System.out.println("從 AI 回應中提取到 " + recommendations.size() + " 個飲品推薦");
        
        // 2. 如果 AI 提取的推薦不足，根據成分關係進行補充
        if (recommendations.size() < maxCount) {
            List<String> ingredientBasedDrinks = findDrinksByIngredientMentions(aiResponse);
            for (String drink : ingredientBasedDrinks) {
                if (!recommendations.contains(drink) && recommendations.size() < maxCount) {
                    recommendations.add(drink);
                    System.out.println("根據成分關係補充推薦：" + extractDrinkName(drink));
                }
            }
        }
        
        // 3. 如果還是不足，根據心情補充推薦（但優先級較低）
        if (recommendations.size() < maxCount) {
            List<String> moodBasedDrinks = getDrinksByMood(mood);
            
            // 過濾掉已存在的推薦，並根據與 AI 回應的相關性排序
            List<String> additionalDrinks = moodBasedDrinks.stream()
                .filter(drink -> !recommendations.contains(drink))
                .sorted((drink1, drink2) -> {
                    double score1 = calculateRelevanceScore(drink1, aiResponse, mood);
                    double score2 = calculateRelevanceScore(drink2, aiResponse, mood);
                    return Double.compare(score2, score1);
                })
                .collect(Collectors.toList());
            
            int needed = maxCount - recommendations.size();
            List<String> selectedMoodDrinks = additionalDrinks.stream()
                .limit(needed)
                .collect(Collectors.toList());
                
            recommendations.addAll(selectedMoodDrinks);
            System.out.println("根據心情補充 " + selectedMoodDrinks.size() + " 個推薦");
        }
        
        // 4. 最後的備用方案：如果仍然不足，添加高評分的受歡迎飲品
        if (recommendations.size() < Math.max(1, maxCount / 2)) { // 至少要有一半的推薦數量
            List<String> popularDrinks = getPopularDrinks();
            List<String> additionalDrinks = popularDrinks.stream()
                .filter(drink -> !recommendations.contains(drink))
                .collect(Collectors.toList());
            
            Collections.shuffle(additionalDrinks);
            int needed = Math.max(1, maxCount / 2) - recommendations.size();
            recommendations.addAll(additionalDrinks.stream()
                .limit(needed)
                .collect(Collectors.toList()));
            System.out.println("使用備用推薦補充至最少推薦數量");
        }
        
        List<String> finalRecommendations = recommendations.stream()
            .limit(maxCount)
            .collect(Collectors.toList());
            
        System.out.println("最終推薦列表包含 " + finalRecommendations.size() + " 個飲品");
        
        return finalRecommendations;
    }
    
    // 新增：根據 AI 回應中提到的成分尋找相關飲品
    public static List<String> findDrinksByIngredientMentions(String aiResponse) {
        List<String> ingredientBasedDrinks = new ArrayList<>();
        String lowerResponse = aiResponse.toLowerCase();
        
        // 檢查 AI 回應中是否提到我們定義的成分
        Map<String, List<String>> relationships = getIngredientRelationships();
        for (String ingredient : relationships.keySet()) {
            if (lowerResponse.contains(ingredient)) {
                List<String> relatedDrinks = findDrinksByIngredientRelationship(ingredient);
                for (String drink : relatedDrinks) {
                    if (!ingredientBasedDrinks.contains(drink)) {
                        ingredientBasedDrinks.add(drink);
                    }
                }
            }
        }
        
        return ingredientBasedDrinks;
    }
    
    // 新增：獲取熱門飲品列表
    public static List<String> getPopularDrinks() {
        Map<String, List<String>> drinksMenu = getDrinksMenu();
        List<String> popularDrinks = new ArrayList<>();
        
        // 從各個熱門分類中選擇代表性飲品
        popularDrinks.addAll(drinksMenu.get("milk teas").stream().limit(3).collect(Collectors.toList()));
        popularDrinks.addAll(drinksMenu.get("fresh juices").stream().limit(2).collect(Collectors.toList()));
        popularDrinks.addAll(drinksMenu.get("fine teas").stream().limit(2).collect(Collectors.toList()));
        popularDrinks.addAll(drinksMenu.get("smoothies").stream().limit(2).collect(Collectors.toList()));
        popularDrinks.addAll(drinksMenu.get("specials").stream().limit(2).collect(Collectors.toList()));
        
        return popularDrinks;
    }
    
    // 新增：獲取成分關係對應表
    public static Map<String, List<String>> getIngredientRelationships() {
        Map<String, List<String>> relationships = new HashMap<>();
        
        // 巧克力應該含有阿華田
        relationships.put("巧克力", Arrays.asList("阿華田"));
        
        // 鮮奶應該含有奶茶
        relationships.put("鮮奶", Arrays.asList("奶茶"));
        
        // 阿華田應該含有巧克力
        relationships.put("阿華田", Arrays.asList("巧克力"));
        
        // 抹茶應該含有抹茶
        relationships.put("抹茶", Arrays.asList("抹茶"));
        
        // 綠茶應該含有綠茶
        relationships.put("綠茶", Arrays.asList("綠茶"));
        
        // 紅茶應該含有紅茶
        relationships.put("紅茶", Arrays.asList("紅茶"));
        
        // 烏龍茶應該含有烏龍茶
        relationships.put("烏龍茶", Arrays.asList("烏龍"));
        
        // 冰沙應該含有冰沙
        relationships.put("冰沙", Arrays.asList("冰沙"));
        
        return relationships;
    }
    
    // 新增：根據成分關係找到相關飲品
    public static List<String> findDrinksByIngredientRelationship(String ingredient) {
        List<String> relatedDrinks = new ArrayList<>();
        List<String> allDrinks = getAllDrinks();
        Map<String, List<String>> relationships = getIngredientRelationships();
        
        // 直接包含該成分的飲品
        for (String drink : allDrinks) {
            String drinkName = extractDrinkName(drink);
            if (drinkName.contains(ingredient)) {
                relatedDrinks.add(drink);
            }
        }
        
        // 根據成分關係尋找相關飲品
        if (relationships.containsKey(ingredient)) {
            List<String> relatedIngredients = relationships.get(ingredient);
            for (String relatedIngredient : relatedIngredients) {
                for (String drink : allDrinks) {
                    String drinkName = extractDrinkName(drink);
                    if (drinkName.contains(relatedIngredient) && !relatedDrinks.contains(drink)) {
                        relatedDrinks.add(drink);
                    }
                }
            }
        }
        
        return relatedDrinks;
    }
    
    // 新增：檢查飲品是否包含特定成分或相關成分
    public static boolean drinkContainsIngredientOrRelated(String drinkName, String ingredient) {
        String cleanDrinkName = extractDrinkName(drinkName);
        
        // 直接包含該成分
        if (cleanDrinkName.contains(ingredient)) {
            return true;
        }
        
        // 檢查是否包含相關成分
        Map<String, List<String>> relationships = getIngredientRelationships();
        if (relationships.containsKey(ingredient)) {
            List<String> relatedIngredients = relationships.get(ingredient);
            for (String relatedIngredient : relatedIngredients) {
                if (cleanDrinkName.contains(relatedIngredient)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // 新增：計算飲品與 AI 回應的相關性分數
    public static double calculateRelevanceScore(String drinkName, String aiResponse, String mood) {
        double score = 0.0;
        String lowerDrinkName = extractDrinkName(drinkName).toLowerCase();
        String lowerResponse = aiResponse.toLowerCase();
        
        // 1. 直接名稱匹配 (35%)
        if (lowerResponse.contains(lowerDrinkName)) {
            score += 0.35;
        }
        
        // 2. 關鍵字匹配 (25%)
        String[] drinkKeywords = lowerDrinkName.split("[/、\\s]");
        for (String keyword : drinkKeywords) {
            if (!keyword.trim().isEmpty() && lowerResponse.contains(keyword.trim())) {
                score += 0.25 / drinkKeywords.length;
            }
        }
        
        // 3. 成分關係匹配 (20%) - 新增
        for (String ingredient : getIngredientRelationships().keySet()) {
            if (lowerResponse.contains(ingredient) && drinkContainsIngredientOrRelated(drinkName, ingredient)) {
                score += 0.2;
                break;
            }
        }
        
        // 4. 心情匹配 (15%)
        List<String> moodDrinks = getDrinksByMood(mood);
        if (moodDrinks.contains(drinkName)) {
            score += 0.15;
        }
        
        // 5. 語義匹配 (5%)
        List<String> semanticMatches = findDrinksBySemanticKeywords(aiResponse);
        if (semanticMatches.contains(drinkName)) {
            score += 0.05;
        }
        
        return Math.min(score, 1.0);
    }
}
