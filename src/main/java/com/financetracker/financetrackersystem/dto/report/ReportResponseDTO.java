package com.financetracker.financetrackersystem.dto.report;

import java.math.BigDecimal;
import java.util.Map;

import com.mongodb.annotations.Sealed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Sealed
public class ReportResponseDTO {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryWiseIncome;
    private Map<String, BigDecimal> categoryWiseExpenses;
    private BigDecimal totalSubscriptionExpenses;
    private Map<String, BigDecimal> subscriptionExpensesByName;

}
