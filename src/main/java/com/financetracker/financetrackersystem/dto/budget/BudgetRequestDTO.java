package com.financetracker.financetrackersystem.dto.budget;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetRequestDTO {

    private String category;
    private Double amount;
    private String currency;
    private LocalDateTime startDate; 
    private LocalDateTime endDate; 


}
