package com.financetracker.financetrackersystem.service.budget;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.budget.BudgetRequestDTO;
import com.financetracker.financetrackersystem.dto.budget.BudgetResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.budget.BudgetEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.repository.BudgetRepository;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyExchangeService currencyExchangeService;
    private final CategoryRepository categoryRepository;
    private final JWTService jwtService;

    // Create Budget
    public BudgetResponseDTO createBudget(BudgetRequestDTO req, String userToken) {
        String username = jwtService.getUsername(userToken);

        if (!isValidCurrency(req.getCurrency())) {
            return new BudgetResponseDTO(null,
                    "Invalid currency code. Must be a valid ISO 4217 code (e.g., USD, EUR, LKR).");
        }

        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(req.getCategory());
        if (!categoryOptional.isPresent()) {
            return new BudgetResponseDTO(null, "Category not found with ID: " + req.getCategory());
        }

        //Convert transaction amount to a base currency
        String baseCurrency = "USD";
        BigDecimal convertedAmount = BigDecimal.valueOf(req.getAmount());

        if (!req.getCurrency().equalsIgnoreCase(baseCurrency)) {
            try {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(req.getCurrency(), baseCurrency);
                convertedAmount = convertedAmount.multiply(exchangeRate);
            } catch (Exception e) {
                return new BudgetResponseDTO(null, "Currency conversion failed: " + e.getMessage());
            }
        }

        BudgetEntity budget = new BudgetEntity();
        budget.setUsername(username);
        budget.setAmount(convertedAmount.doubleValue());
        budget.setCategory(req.getCategory());
        budget.setCurrency(baseCurrency);
        budget.setStartDate(req.getStartDate());
        budget.setEndDate(req.getEndDate());

        BudgetEntity savedBudget = budgetRepository.save(budget);
        if (savedBudget.getId() == null) {
            return new BudgetResponseDTO(null, "Unable to create Budget");
        }
        return new BudgetResponseDTO("Budget created successfully", null);
    }

    // Get Budget
    public List<BudgetEntity> getUserAllBudget(String username) {
        return budgetRepository.findByUsername(username);
    }

    // Get Budget for admin
    public List<BudgetEntity> getAdminAllBudget(String userRole) {
        if (!userRole.equals("Admin")) {
            throw new IllegalArgumentException("You are not authorized to access this resource");
        }
        return budgetRepository.findAll();
    }

    // Update Budget
    public BudgetResponseDTO updateBudget(BudgetRequestDTO req, String budgetId) {
        Optional<BudgetEntity> budgetOptional = budgetRepository.findById(budgetId);
        if (budgetOptional.isEmpty()) {
            return new BudgetResponseDTO(null, "Unable to find Budget");
        }
        
        if (!isValidCurrency(req.getCurrency())) {
            return new BudgetResponseDTO(null,
                    "Invalid currency code. Must be a valid ISO 4217 code (e.g., USD, EUR, LKR).");
        }

        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(req.getCategory());
        if (!categoryOptional.isPresent()) {
            return new BudgetResponseDTO(null, "Category not found with ID: " + req.getCategory());
        }

        BudgetEntity budget = budgetOptional.get();
        budget.setAmount(req.getAmount());
        budget.setCategory(req.getCategory());
        budget.setCurrency(req.getCurrency().toUpperCase());
        budget.setStartDate(req.getStartDate());
        budget.setEndDate(req.getEndDate());

        budgetRepository.save(budget);
        return new BudgetResponseDTO("Budget updated successfully, changes saved.", null);
    }

    // Delete Budget
    public BudgetResponseDTO deleteBudget(String userToken, String verify, String budgetId) {
        String username = jwtService.getUsername(userToken);

        if (!"yes".equalsIgnoreCase(verify)) {
            return new BudgetResponseDTO("Type yes to delete", "Verify error");
        }
        Optional<BudgetEntity> budgetOptional = budgetRepository.findById(budgetId);

        if (budgetOptional.isEmpty()) {
            return new BudgetResponseDTO("The Budget you're looking for does not exist.", null);
        }

        BudgetEntity budget = budgetOptional.get();
        if (!budget.getUsername().equals(username)) {
            return new BudgetResponseDTO("You are not authorized to delete this transaction.", null);
        }

        budgetRepository.deleteById(budgetId);

        return new BudgetResponseDTO("Budget deleted successfully.", null);
    }

    // Currency check
    private boolean isValidCurrency(String currencyCode) {
        try {
            Currency.getInstance(currencyCode.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //Notify users when nearing or exceeding budgets
    public List<String> checkBudgetThresholds(String username) {
        List<BudgetEntity> budgets = budgetRepository.findByUsername(username);
        LocalDateTime now = LocalDateTime.now();

        return budgets.stream()
                .filter(budget -> now.isAfter(budget.getStartDate()) && now.isBefore(budget.getEndDate()))
                .map(budget -> {
                    String categoryName = getCategoryName(budget.getCategory());

                    double spentAmount = getSpentAmountForCategory(username, budget.getCategory());
                    double budgetAmount = budget.getAmount();
                    double threshold = budgetAmount * 0.8; 

                    if (spentAmount >= budgetAmount) {
                        return String.format("You have exceeded your budget for %s!", categoryName);
                    } else if (spentAmount >= threshold) {
                        return String.format("You are nearing your budget limit for %s!", categoryName);
                    } else {
                        return null;
                    }
                })
                .filter(message -> message != null)
                .collect(Collectors.toList());
    }

    //Provide budget adjustment recommendations based on spending trends
    public String getBudgetRecommendation(String username) {
        List<BudgetEntity> budgets = budgetRepository.findByUsername(username);
        LocalDateTime now = LocalDateTime.now();

        double totalSpent = budgets.stream()
                .filter(budget -> now.isAfter(budget.getStartDate()) && now.isBefore(budget.getEndDate()))
                .mapToDouble(budget -> getSpentAmountForCategory(username, budget.getCategory()))
                .sum();

        double totalBudget = budgets.stream()
                .filter(budget -> now.isAfter(budget.getStartDate()) && now.isBefore(budget.getEndDate()))
                .mapToDouble(BudgetEntity::getAmount)
                .sum();

        if (totalSpent > totalBudget) {
            return "You are overspending. Consider reducing expenses in high-spend categories.";
        } else if (totalSpent >= totalBudget * 0.9) {
            return "You are close to your budget limit. Review your spending to avoid overspending.";
        } else {
            return "Your spending is within budget. Keep up the good work!";
        }
    }

    private double getSpentAmountForCategory(String username, String category) {
        List<TransactionEntity> transactions = fetchTransactionsForUser(username);

        // Calculate total spent amount for the given category
        BigDecimal totalSpent = transactions.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType())) // Filter only expenses
                .filter(t -> category.equalsIgnoreCase(t.getCategory())) // Filter by category
                .map(t -> BigDecimal.valueOf(t.getAmount())) // Map to BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum all amounts

        return totalSpent.doubleValue();
    }

    private List<TransactionEntity> fetchTransactionsForUser(String username) {
        return transactionRepository.findByUsername(username);
    }

    private String getCategoryName(String categoryId) {
        Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
        return category.map(CategoryEntity::getCategoryName).orElse("Unknown Category");
    }
}