package com.financetracker.financetrackersystem.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.financetracker.financetrackersystem.entity.CategoryEntity;

public interface CategoryRepository extends MongoRepository<CategoryEntity, String>{

}
