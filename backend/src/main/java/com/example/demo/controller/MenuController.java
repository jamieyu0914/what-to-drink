package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MenuController {

    private final Map<String, List<String>> drinksMenu;

    public MenuController() {
        this.drinksMenu = initializeDrinksMenu();
    }

    private Map<String, List<String>> initializeDrinksMenu() {
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

    @GetMapping("/drinks/menu")
    public ResponseEntity<Map<String, List<String>>> getDrinksMenu() {
        return ResponseEntity.ok(drinksMenu);
    }

    @PostMapping("/drinks/auto-select")
    public ResponseEntity<Map<String, String>> getAutoSelectedDrink(@RequestBody List<String> selectedDrinks) {
        if (selectedDrinks == null || selectedDrinks.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "請至少選擇一項飲料"));
        }

        Random random = new Random();
        String autoSelected = selectedDrinks.get(random.nextInt(selectedDrinks.size()));

        return ResponseEntity.ok(Map.of("result", autoSelected));
    }
}