package com.example.demo.repository;

import com.example.demo.model.UserPreference;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    List<UserPreference> findByUser(User user);
    List<UserPreference> findByUserAndPreferenceType(User user, String preferenceType);
    Optional<UserPreference> findByUserAndPreferenceTypeAndPreferenceValue(User user, String preferenceType, String preferenceValue);
    void deleteByUser(User user);
}
