package com.example.demo.service;

import com.example.demo.dto.FavoriteDrinkRequest;
import com.example.demo.model.FavoriteDrink;
import com.example.demo.model.User;
import com.example.demo.repository.FavoriteDrinkRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FavoriteDrinkService {
    
    @Autowired
    private FavoriteDrinkRepository favoriteDrinkRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public FavoriteDrink addFavoriteDrink(String username, FavoriteDrinkRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        
        User user = userOpt.get();
        
        // 檢查是否已經收藏
        if (favoriteDrinkRepository.existsByUserAndDrinkName(user, request.getDrinkName())) {
            throw new RuntimeException("此飲品已在收藏清單中");
        }
        
        FavoriteDrink favoriteDrink = new FavoriteDrink(user, request.getDrinkName(), request.getDrinkCategory());
        favoriteDrink.setDrinkDescription(request.getDrinkDescription());
        favoriteDrink.setPrice(request.getPrice());
        favoriteDrink.setTags(request.getTags());
        favoriteDrink.setNotes(request.getNotes());
        
        return favoriteDrinkRepository.save(favoriteDrink);
    }
    
    public void removeFavoriteDrink(String username, String drinkName) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        
        User user = userOpt.get();
        Optional<FavoriteDrink> favDrinkOpt = favoriteDrinkRepository.findByUserAndDrinkName(user, drinkName);
        
        if (favDrinkOpt.isEmpty()) {
            throw new RuntimeException("收藏清單中找不到此飲品");
        }
        
        favoriteDrinkRepository.delete(favDrinkOpt.get());
    }
    
    public List<FavoriteDrink> getUserFavoriteDrinks(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        return favoriteDrinkRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
    }
    
    public List<FavoriteDrink> getUserFavoriteDrinksByCategory(String username, String category) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return List.of();
        }
        
        return favoriteDrinkRepository.findByUserAndDrinkCategoryOrderByCreatedAtDesc(userOpt.get(), category);
    }
    
    public boolean isFavoriteDrink(String username, String drinkName) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        return favoriteDrinkRepository.existsByUserAndDrinkName(userOpt.get(), drinkName);
    }
    
    public long getFavoriteDrinkCount(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return 0;
        }
        
        return favoriteDrinkRepository.countByUser(userOpt.get());
    }
    
    public FavoriteDrink updateFavoriteDrinkNotes(String username, String drinkName, String notes) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        
        User user = userOpt.get();
        Optional<FavoriteDrink> favDrinkOpt = favoriteDrinkRepository.findByUserAndDrinkName(user, drinkName);
        
        if (favDrinkOpt.isEmpty()) {
            throw new RuntimeException("收藏清單中找不到此飲品");
        }
        
        FavoriteDrink favoriteDrink = favDrinkOpt.get();
        favoriteDrink.setNotes(notes);
        
        return favoriteDrinkRepository.save(favoriteDrink);
    }
}
