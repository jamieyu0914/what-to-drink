package com.example.demo.controller;

import com.example.demo.util.MenuDataUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MenuController {

    private final Map<String, List<String>> drinksMenu;

    public MenuController() {
        this.drinksMenu = MenuDataUtil.getDrinksMenu();
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