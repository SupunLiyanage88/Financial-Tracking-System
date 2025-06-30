package com.financetracker.financetrackersystem.entity.budget;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "budgets")  
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetEntity {
    @Id
    private String id;
    private String username;  
    private String category;  
    private Double amount;    
    private String currency;  
    private LocalDateTime startDate; 
    private LocalDateTime endDate;  
}

