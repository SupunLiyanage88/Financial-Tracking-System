package com.financetracker.financetrackersystem.entity.subscription;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "subscriptions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionEntity {

    @Id
    private String id;

    private String username;
    private String subscriptionName;
    private String transactionType; // income/expense
    private Double amount;
    private String currency;
    private String recurrencePattern; // daily, weekly, monthly
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String category;
    private LocalDateTime lastTransactionDate;
    

}
