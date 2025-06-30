package com.financetracker.financetrackersystem.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.financetracker.financetrackersystem.entity.budget.BudgetEntity;

public interface BudgetRepository extends MongoRepository<BudgetEntity, String> {

    List<BudgetEntity> findByUsername(String username);
    List<BudgetEntity> findByUsernameAndCategory(String username, String category);

}
