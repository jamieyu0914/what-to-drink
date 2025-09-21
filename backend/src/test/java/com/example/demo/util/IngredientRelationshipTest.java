package com.example.demo.util;

import java.util.List;
import java.util.Map;

/**
 * 測試成分關係功能的簡單測試類
 */
public class IngredientRelationshipTest {
    
    public static void main(String[] args) {
        System.out.println("=== 測試成分關係功能 ===");
        
        // 測試 1: 檢查成分關係對應表
        testIngredientRelationships();
        
        // 測試 2: 檢查根據成分尋找相關飲品
        testFindDrinksByIngredient();
        
        // 測試 3: 檢查成分關係匹配
        testIngredientMatching();
        
        // 測試 4: 測試 AI 回應中的成分識別
        testAIResponseIngredientDetection();
    }
    
    private static void testIngredientRelationships() {
        System.out.println("\n1. 測試成分關係對應表:");
        Map<String, List<String>> relationships = MenuDataUtil.getIngredientRelationships();
        
        for (Map.Entry<String, List<String>> entry : relationships.entrySet()) {
            System.out.println("   " + entry.getKey() + " → " + entry.getValue());
        }
        
        // 驗證預期的關係
        assert relationships.containsKey("巧克力") : "應該包含巧克力關係";
        assert relationships.containsKey("鮮奶") : "應該包含鮮奶關係";
        assert relationships.containsKey("阿華田") : "應該包含阿華田關係";
        
        System.out.println("   ✓ 成分關係對應表測試通過");
    }
    
    private static void testFindDrinksByIngredient() {
        System.out.println("\n2. 測試根據成分尋找相關飲品:");
        
        // 測試巧克力
        List<String> chocolateDrinks = MenuDataUtil.findDrinksByIngredientRelationship("巧克力");
        System.out.println("   巧克力相關飲品:");
        for (String drink : chocolateDrinks) {
            System.out.println("     - " + MenuDataUtil.extractDrinkName(drink));
        }
        
        // 測試鮮奶
        List<String> milkDrinks = MenuDataUtil.findDrinksByIngredientRelationship("鮮奶");
        System.out.println("   鮮奶相關飲品:");
        for (String drink : milkDrinks) {
            System.out.println("     - " + MenuDataUtil.extractDrinkName(drink));
        }
        
        // 測試阿華田
        List<String> ovaltineDrinks = MenuDataUtil.findDrinksByIngredientRelationship("阿華田");
        System.out.println("   阿華田相關飲品:");
        for (String drink : ovaltineDrinks) {
            System.out.println("     - " + MenuDataUtil.extractDrinkName(drink));
        }
        
        System.out.println("   ✓ 成分飲品搜尋測試完成");
    }
    
    private static void testIngredientMatching() {
        System.out.println("\n3. 測試成分關係匹配:");
        
        // 測試案例
        String[] testDrinks = {
            "【香醇鮮奶系列】巧克力鮮奶",
            "【香醇鮮奶系列】阿華田鮮奶",
            "【奶茶系列】奶茶",
            "【奶茶系列】珍珠奶茶"
        };
        
        for (String drink : testDrinks) {
            System.out.println("   測試飲品: " + MenuDataUtil.extractDrinkName(drink));
            
            boolean containsChocolate = MenuDataUtil.drinkContainsIngredientOrRelated(drink, "巧克力");
            boolean containsMilk = MenuDataUtil.drinkContainsIngredientOrRelated(drink, "鮮奶");
            boolean containsOvaltine = MenuDataUtil.drinkContainsIngredientOrRelated(drink, "阿華田");
            
            System.out.println("     包含巧克力或相關: " + containsChocolate);
            System.out.println("     包含鮮奶或相關: " + containsMilk);
            System.out.println("     包含阿華田或相關: " + containsOvaltine);
        }
        
        System.out.println("   ✓ 成分匹配測試完成");
    }
    
    private static void testAIResponseIngredientDetection() {
        System.out.println("\n4. 測試 AI 回應中的成分識別:");
        
        String[] testResponses = {
            "我想要喝巧克力味的飲品",
            "推薦一些鮮奶類的飲料",
            "有阿華田口味的嗎？",
            "想要甜甜的巧克力飲品"
        };
        
        for (String response : testResponses) {
            System.out.println("   測試回應: \"" + response + "\"");
            List<String> ingredientDrinks = MenuDataUtil.findDrinksByIngredientMentions(response);
            
            if (!ingredientDrinks.isEmpty()) {
                System.out.println("     找到的相關飲品:");
                for (String drink : ingredientDrinks) {
                    System.out.println("       - " + MenuDataUtil.extractDrinkName(drink));
                }
            } else {
                System.out.println("     沒有找到相關飲品");
            }
        }
        
        System.out.println("   ✓ AI 回應成分識別測試完成");
    }
}