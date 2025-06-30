package com.financetracker.financetrackersystem.dto.subscription;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionRequestDTO {

    private String subscriptionName;
    private String transactionType; //income/expense
    private Double amount;
    private String currency;
    private String recurrencePattern; //daily, weekly, monthly
    private LocalDateTime endDate;
    private String category;

}
