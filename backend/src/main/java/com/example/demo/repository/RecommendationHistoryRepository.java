package com.example.demo.repository;

import com.example.demo.model.RecommendationHistory;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {
    List<RecommendationHistory> findByUserOrderByCreatedAtDesc(User user);
    List<RecommendationHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<RecommendationHistory> findByUserAndCreatedAtAfterOrderByCreatedAtDesc(User user, LocalDateTime after);
    
    @Query("SELECT COUNT(r) FROM RecommendationHistory r WHERE r.user = ?1")
    long countByUser(User user);
    
    @Query("SELECT AVG(r.userRating) FROM RecommendationHistory r WHERE r.user = ?1 AND r.userRating IS NOT NULL")
    Double getAverageRatingByUser(User user);
}
