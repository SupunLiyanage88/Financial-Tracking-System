package com.financetracker.financetrackersystem.service.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.report.ReportRequestDTO;
import com.financetracker.financetrackersystem.dto.report.ReportResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.SubscriptionRepository;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.service.JWTService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {

    private final JWTService jwtService;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ReportResponseDTO generateReport(
        ReportRequestDTO req,
        String userToken
    ) {
        String username = jwtService.getUsername(userToken);

        // Fetch transactions within the date range
        List<TransactionEntity> transactions = transactionRepository.findByUsernameAndDateBetween(
            username, 
            req.getStartDate(), 
            req.getEndDate()
        );

        // Fetch subscriptions within the date range
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findByUsernameAndStartDateBetween(
            username,
            req.getStartDate(),
            req.getEndDate()
        );

        // Calculate total income
        BigDecimal totalIncome = transactions.stream()
            .filter(t -> "income".equalsIgnoreCase(t.getType()))
            .map(t -> BigDecimal.valueOf(t.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total expenses
        BigDecimal totalExpenses = transactions.stream()
            .filter(t -> "expense".equalsIgnoreCase(t.getType()))
            .map(t -> BigDecimal.valueOf(t.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate net balance
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // Group expenses by category ID
        Map<String, BigDecimal> categoryWiseExpensesById = transactions.stream()
            .filter(t -> "expense".equalsIgnoreCase(t.getType()))
            .collect(Collectors.groupingBy(
                TransactionEntity::getCategory,
                Collectors.reducing(BigDecimal.ZERO,
                    t -> BigDecimal.valueOf(t.getAmount()),
                    BigDecimal::add )
            ));

        // Group income by category ID
        Map<String, BigDecimal> categoryWiseIncomeById = transactions.stream()
            .filter(t -> "income".equalsIgnoreCase(t.getType()))
            .collect(Collectors.groupingBy(
                TransactionEntity::getCategory,
                Collectors.reducing(BigDecimal.ZERO,
                    t -> BigDecimal.valueOf(t.getAmount()),
                    BigDecimal::add )
            ));

        // Fetch category names from repository
        Map<String, String> categoryIdToNameMap = categoryRepository.findAll()
            .stream()
            .collect(Collectors.toMap(CategoryEntity::getId, CategoryEntity::getCategoryName));

        // Replace category IDs with category names for expenses
        Map<String, BigDecimal> categoryWiseExpenses = categoryWiseExpensesById.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> categoryIdToNameMap.getOrDefault(entry.getKey(), "Unknown"),
                Map.Entry::getValue
            ));

        // Replace category IDs with category names for income
        Map<String, BigDecimal> categoryWiseIncome = categoryWiseIncomeById.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> categoryIdToNameMap.getOrDefault(entry.getKey(), "Unknown"),
                Map.Entry::getValue
            ));

        // Calculate total subscription expenses
        BigDecimal totalSubscriptionExpenses = subscriptions.stream()
            .map(s -> BigDecimal.valueOf(s.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group subscription expenses by subscription name
        Map<String, BigDecimal> subscriptionExpensesByName = subscriptions.stream()
            .collect(Collectors.groupingBy(
                SubscriptionEntity::getSubscriptionName,
                Collectors.reducing(BigDecimal.ZERO,
                    s -> BigDecimal.valueOf(s.getAmount()),
                    BigDecimal::add )
            ));

        return new ReportResponseDTO(
            totalIncome, 
            totalExpenses, 
            netBalance, 
            categoryWiseIncome, 
            categoryWiseExpenses, 
            totalSubscriptionExpenses, 
            subscriptionExpensesByName
        );
    }
}
