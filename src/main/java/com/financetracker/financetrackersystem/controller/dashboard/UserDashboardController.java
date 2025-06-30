package com.financetracker.financetrackersystem.controller.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.budget.BudgetService;
import com.financetracker.financetrackersystem.service.transaction.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-dashboard")
@AllArgsConstructor
public class UserDashboardController {

    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final JWTService jwtService;

    @GetMapping("/summary")
    public Map<String, Object> getDashboardSummary(HttpServletRequest request) {
        String jwtToken = jwtService.extractJwtFromCookie(request);    
        List<TransactionEntity> transactions = transactionService.getUserAllTransaction(jwtToken);

        double totalIncome = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income"))
                .mapToDouble(TransactionEntity::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("expense"))
                .mapToDouble(TransactionEntity::getAmount)
                .sum();

        double netSavings = totalIncome - totalExpense;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netSavings", netSavings);
        summary.put("totalTransactions", transactions.size());

        // Fixing the budget threshold addition
        Object budgetThresholdStatus = budgetService.getBudgetRecommendation(jwtToken);
        String statusString = budgetThresholdStatus.toString();
        summary.put("budgetStatus", statusString); // Ensure this key makes sense

        return summary;
    }


    @GetMapping("/transactions")
    public List<TransactionEntity> getRecentTransactions(HttpServletRequest request) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        return transactionService.getUserAllTransaction(jwtToken);
    }


}
