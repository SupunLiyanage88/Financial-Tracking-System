package com.financetracker.financetrackersystem.repository;


import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.financetracker.financetrackersystem.entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByUsername(String username);

}
