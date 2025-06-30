package com.financetracker.financetrackersystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;

public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {

    List<TransactionEntity> findByUsername(String username);
    List<TransactionEntity> findByUsernameAndDateBetween(String username, LocalDateTime start, LocalDateTime end);

}
