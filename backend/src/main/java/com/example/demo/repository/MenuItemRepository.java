package com.example.demo.repository;

import com.example.demo.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByAvailableTrue();
    List<MenuItem> findByCategoryAndAvailableTrue(String category);
    List<MenuItem> findByNameContainingIgnoreCaseAndAvailableTrue(String name);
    List<MenuItem> findByMoodAndAvailableTrue(String mood);
    List<MenuItem> findBySeasonAndAvailableTrue(String season);
    List<MenuItem> findByTemperatureAndAvailableTrue(String temperature);
    List<MenuItem> findBySweetnessAndAvailableTrue(String sweetness);
    List<MenuItem> findByCaffeineAndAvailableTrue(String caffeine);
    List<MenuItem> findByFlavorAndAvailableTrue(String flavor);
    
    @Query("SELECT m FROM MenuItem m WHERE m.available = true AND " +
           "(m.mood = ?1 OR m.season = ?2 OR m.temperature = ?3 OR m.sweetness = ?4 OR m.caffeine = ?5 OR m.flavor = ?6)")
    List<MenuItem> findByMultipleCriteria(String mood, String season, String temperature, 
                                        String sweetness, String caffeine, String flavor);
    
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.available = true")
    List<String> findAllCategories();
}
