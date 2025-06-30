package com.financetracker.financetrackersystem.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.financetracker.financetrackersystem.entity.goal.GoalEntity;

public interface GoalRepository extends MongoRepository<GoalEntity, String> {
    List<GoalEntity> findByUsername(String username); // Find goals by username
}