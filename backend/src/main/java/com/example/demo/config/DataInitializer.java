package com.example.demo.config;

import com.example.demo.model.MenuItem;
import com.example.demo.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (menuItemRepository.count() == 0) {
            initializeMenuItems();
        }
    }
    
    private void initializeMenuItems() {
        // 咖啡類
        addMenuItem("美式咖啡", "咖啡", "經典美式黑咖啡，口感純粹", 45.0, "hot", "none", "high", "bitter", "咖啡豆、水", "all", "energetic");
        addMenuItem("拿鐵咖啡", "咖啡", "香濃牛奶與濃縮咖啡的完美結合", 65.0, "hot", "low", "medium", "creamy", "咖啡豆、牛奶", "all", "focused");
        addMenuItem("卡布奇諾", "咖啡", "濃縮咖啡配奶泡，口感豐富", 60.0, "hot", "low", "medium", "creamy", "咖啡豆、牛奶", "all", "cozy");
        addMenuItem("冰美式", "咖啡", "清爽的冰鎮美式咖啡", 50.0, "cold", "none", "high", "bitter", "咖啡豆、水、冰塊", "summer", "refreshed");
        addMenuItem("焦糖瑪奇朵", "咖啡", "甜美的焦糖風味拿鐵", 70.0, "hot", "high", "medium", "sweet", "咖啡豆、牛奶、焦糖糖漿", "all", "happy");
        
        // 茶類
        addMenuItem("鐵觀音", "茶", "香醇的烏龍茶，回甘無窮", 40.0, "hot", "none", "low", "fragrant", "鐵觀音茶葉", "all", "relaxed");
        addMenuItem("茉莉綠茶", "茶", "清香的茉莉花茶", 35.0, "hot", "none", "low", "floral", "綠茶、茉莉花", "spring", "calm");
        addMenuItem("奶茶", "茶", "香濃的紅茶配牛奶", 55.0, "hot", "medium", "low", "creamy", "紅茶、牛奶、糖", "all", "cozy");
        addMenuItem("抹茶拿鐵", "茶", "日式抹茶與牛奶的絕配", 65.0, "hot", "medium", "medium", "earthy", "抹茶粉、牛奶", "all", "zen");
        addMenuItem("冰紅茶", "茶", "清爽的冰紅茶", 35.0, "cold", "low", "low", "refreshing", "紅茶、冰塊", "summer", "refreshed");
        
        // 果汁類
        addMenuItem("鮮榨柳橙汁", "果汁", "新鮮柳橙現榨，維C豐富", 45.0, "cold", "medium", "none", "citrus", "新鮮柳橙", "all", "energetic");
        addMenuItem("蘋果汁", "果汁", "香甜的蘋果汁", 40.0, "cold", "high", "none", "fruity", "新鮮蘋果", "all", "refreshed");
        addMenuItem("葡萄汁", "果汁", "酸甜的葡萄汁", 45.0, "cold", "high", "none", "fruity", "新鮮葡萄", "summer", "happy");
        addMenuItem("檸檬蜂蜜茶", "果汁", "酸甜的檸檬蜂蜜茶", 50.0, "hot", "medium", "none", "citrus", "檸檬、蜂蜜、水", "all", "soothing");
        
        // 特色飲品
        addMenuItem("草莓奶昔", "奶昔", "香甜的草莓奶昔", 75.0, "cold", "high", "none", "fruity", "草莓、牛奶、冰淇淋", "summer", "happy");
        addMenuItem("巧克力奶昔", "奶昔", "濃郁的巧克力奶昔", 80.0, "cold", "high", "none", "chocolate", "巧克力、牛奶、冰淇淋", "all", "indulgent");
        addMenuItem("氣泡水", "氣泡水", "清爽的氣泡水", 25.0, "cold", "none", "none", "refreshing", "碳酸水", "summer", "refreshed");
        addMenuItem("蜂蜜檸檬氣泡水", "氣泡水", "帶有蜂蜜檸檬味的氣泡水", 35.0, "cold", "low", "none", "citrus", "碳酸水、檸檬、蜂蜜", "summer", "refreshed");
        
        // 季節性飲品
        addMenuItem("熱巧克力", "熱飲", "溫暖的熱巧克力", 55.0, "hot", "high", "none", "chocolate", "巧克力、牛奶", "winter", "cozy");
        addMenuItem("薑茶", "茶", "溫暖的薑茶", 30.0, "hot", "low", "none", "spicy", "生薑、紅糖", "winter", "warming");
        addMenuItem("冰沙", "冰品", "消暑的水果冰沙", 60.0, "cold", "medium", "none", "fruity", "各種水果、冰塊", "summer", "cooling");
    }
    
    private void addMenuItem(String name, String category, String description, Double price,
                           String temperature, String sweetness, String caffeine, String flavor,
                           String ingredients, String season, String mood) {
        MenuItem item = new MenuItem(name, category, description, price);
        item.setTemperature(temperature);
        item.setSweetness(sweetness);
        item.setCaffeine(caffeine);
        item.setFlavor(flavor);
        item.setIngredients(ingredients);
        item.setSeason(season);
        item.setMood(mood);
        item.setAvailable(true);
        
        menuItemRepository.save(item);
    }
}
