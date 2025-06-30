package com.financetracker.financetrackersystem.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.transaction.TransactionRequestDTO;
import com.financetracker.financetrackersystem.dto.transaction.TransactionResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final JWTService jwtService;
    private final CurrencyExchangeService currencyExchangeService;
    private final CategoryRepository categoryRepository;

    //Add Transaction
    public TransactionResponseDTO addTransaction(
            TransactionRequestDTO req,
            String userToken) {
        String username = jwtService.getUsername(userToken);

        if (!req.getType().equalsIgnoreCase("income") && !req.getType().equalsIgnoreCase("expense")) {
            return new TransactionResponseDTO(null, "Invalid transaction type. Must be 'income' or 'expense'.");
        }

        if (!isValidCurrency(req.getCurrency())) {
            return new TransactionResponseDTO(null,
                    "Invalid currency code. Must be a valid ISO 4217 code (e.g., USD, EUR, LKR).");
        }

        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(req.getCategory());
        if (!categoryOptional.isPresent()) {
            return new TransactionResponseDTO(null, "Category not found with ID: " + req.getCategory());
        }

        //Convert transaction amount to a base currency
        String baseCurrency = "USD";
        BigDecimal convertedAmount = BigDecimal.valueOf(req.getAmount());

        if (!req.getCurrency().equalsIgnoreCase(baseCurrency)) {
            try {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(req.getCurrency(), baseCurrency);
                convertedAmount = convertedAmount.multiply(exchangeRate);
            } catch (Exception e) {
                return new TransactionResponseDTO(null, "Currency conversion failed: " + e.getMessage());
            }
        }

        // Create and save transaction
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUsername(username);
        transaction.setType(req.getType().toLowerCase());
        transaction.setAmount(convertedAmount.doubleValue());
        transaction.setCurrency(baseCurrency);
        transaction.setDate(LocalDateTime.now());
        transaction.setCategory(req.getCategory());

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        if (savedTransaction.getId() == null) {
            return new TransactionResponseDTO(null, "Unable to create transaction");
        }
        return new TransactionResponseDTO("Transaction created successfully", null);
    }

    //Get all transaction by user
    public List<TransactionEntity> getUserAllTransaction(String userToken) {
        String username = jwtService.getUsername(userToken);
        return transactionRepository.findByUsername(username);
    }

    //Get all transaction for admin
    public List<TransactionEntity> getAllTransaction(String userToken) {
        String userRole = jwtService.getUserRole(userToken);
        if (!userRole.equals("Admin")) {
            throw new IllegalArgumentException("You are not authorized to access this resource");
        }
        return transactionRepository.findAll();
    }

    //Update transaction
    public TransactionResponseDTO updateTransaction(
            TransactionRequestDTO req,
            String transactionId) {
        Optional<TransactionEntity> transactionOptional = transactionRepository.findById(transactionId);
        if (transactionOptional.isEmpty()) {
            return new TransactionResponseDTO(null, "Transaction not found.");
        }

        if (!req.getType().equalsIgnoreCase("income") && !req.getType().equalsIgnoreCase("expense")) {
            return new TransactionResponseDTO(null, "Invalid transaction type. Must be 'income' or 'expense'.");
        }

        if (!isValidCurrency(req.getCurrency())) {
            return new TransactionResponseDTO(null,
                    "Invalid currency code. Must be a valid ISO 4217 code (e.g., USD, EUR, LKR).");
        }

        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(req.getCategory());
        if (!categoryOptional.isPresent()) {
            return new TransactionResponseDTO(null, "Category not found with ID: " + req.getCategory());
        }

        TransactionEntity transaction = transactionOptional.get();

        //Convert transaction amount to a base currency
        String baseCurrency = "USD";
        BigDecimal convertedAmount = BigDecimal.valueOf(req.getAmount());

        if (!req.getCurrency().equalsIgnoreCase(baseCurrency)) {
            try {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(req.getCurrency(), baseCurrency);
                convertedAmount = convertedAmount.multiply(exchangeRate);
            } catch (Exception e) {
                return new TransactionResponseDTO(null, "Currency conversion failed: " + e.getMessage());
            }
        }

        //Update transaction details
        transaction.setType(req.getType().toLowerCase());
        transaction.setAmount(convertedAmount.doubleValue());
        transaction.setCurrency(baseCurrency);
        transaction.setDate(LocalDateTime.now());
        transaction.setCategory(req.getCategory());

        transactionRepository.save(transaction);

        return new TransactionResponseDTO("Transaction updated successfully.", null);
    }

    //Delete Transaction
    public TransactionResponseDTO deleteTransaction(String userToken, String verify, String transactionId) {
        String username = jwtService.getUsername(userToken);

        if (!"yes".equalsIgnoreCase(verify)) {
            return new TransactionResponseDTO(
                    "Type yes to delete",
                    "Verify error");
        }

        Optional<TransactionEntity> transactionOptional = transactionRepository.findById(transactionId);

        if (transactionOptional.isEmpty()) {
            return new TransactionResponseDTO("The transaction you're looking for does not exist.", null);
        }

        TransactionEntity transaction = transactionOptional.get();
        if (!transaction.getUsername().equals(username)) {
            return new TransactionResponseDTO("You are not authorized to delete this transaction.", null);
        }

        transactionRepository.deleteById(transactionId);

        return new TransactionResponseDTO("Transaction deleted successfully.", null);
    }

    //Recurring Transactions
    public void addRecurringTransaction(SubscriptionEntity subscription) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUsername(subscription.getUsername());
        transaction.setType(subscription.getTransactionType());
        transaction.setAmount(subscription.getAmount());
        transaction.setCurrency(subscription.getCurrency());
        transaction.setDate(LocalDateTime.now());
        transaction.setCategory(subscription.getCategory());

        transactionRepository.save(transaction);
    }

    //User currency changer
    public List<TransactionEntity> exchangeUserTransactions(String userToken, String targetCurrency) {
        String username = jwtService.getUsername(userToken);

        // Fetch all transactions for the user
        List<TransactionEntity> transactions = transactionRepository.findByUsername(username);

        // Convert all transactions to the target currency
        for (TransactionEntity transaction : transactions) {
            BigDecimal amount = BigDecimal.valueOf(transaction.getAmount());
            String currentCurrency = transaction.getCurrency();

            // If the transaction's currency is not already the target currency, convert it
            if (!currentCurrency.equalsIgnoreCase(targetCurrency)) {
                try {
                    // Get the exchange rate from currentCurrency to targetCurrency
                    BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(currentCurrency, targetCurrency);

                    // Convert the amount
                    BigDecimal convertedAmount = amount.multiply(exchangeRate);

                    // Update the transaction with the converted amount and target currency
                    transaction.setAmount(convertedAmount.doubleValue());
                    transaction.setCurrency(targetCurrency.toUpperCase());

                    transactionRepository.save(transaction);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Currency conversion failed: " + e.getMessage());
                }
            }
        }

        return transactions;
    }

    private boolean isValidCurrency(String currencyCode) {
        try {
            Currency.getInstance(currencyCode.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
