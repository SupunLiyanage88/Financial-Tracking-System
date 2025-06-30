package com.financetracker.financetrackersystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;

public interface SubscriptionRepository extends MongoRepository<SubscriptionEntity, String> {

    Optional<SubscriptionEntity> findByUsernameAndId(String username, String id);

    // Fetch all subscriptions within a date range for a specific user
    List<SubscriptionEntity> findByUsernameAndStartDateBetween(String username, LocalDateTime startDate, LocalDateTime endDate);
}
