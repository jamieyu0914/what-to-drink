package com.example.demo.repository;

import com.example.demo.model.FavoriteDrink;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteDrinkRepository extends JpaRepository<FavoriteDrink, Long> {
    List<FavoriteDrink> findByUserOrderByCreatedAtDesc(User user);
    List<FavoriteDrink> findByUserAndDrinkCategoryOrderByCreatedAtDesc(User user, String drinkCategory);
    Optional<FavoriteDrink> findByUserAndDrinkName(User user, String drinkName);
    boolean existsByUserAndDrinkName(User user, String drinkName);
    long countByUser(User user);
}
