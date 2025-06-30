package com.financetracker.financetrackersystem.service.subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financetracker.financetrackersystem.dto.subscription.SubscriptionRequestDTO;
import com.financetracker.financetrackersystem.dto.subscription.SubscriptionResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.SubscriptionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;
import com.financetracker.financetrackersystem.service.email.EmailService;
import com.financetracker.financetrackersystem.service.transaction.TransactionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TransactionService transactionService;
    private final JWTService jwtService;
    private final CurrencyExchangeService currencyExchangeService;
    private final EmailService emailService;
    private final CategoryRepository categoryRepository;


    //Add subscription
   @Transactional
   public SubscriptionResponseDTO addSubscription(SubscriptionRequestDTO req, String userToken) {
       String username = jwtService.getUsername(userToken);
       String baseCurrency = "USD";
       BigDecimal convertedAmount = new BigDecimal(req.getAmount());
          
       Optional<CategoryEntity> categoryOptional = categoryRepository.findById(req.getCategory());
       if (!categoryOptional.isPresent()) {
           return new SubscriptionResponseDTO(null, "Category not found with ID: " + req.getCategory());
       }
   
       if (!req.getCurrency().equalsIgnoreCase(baseCurrency)) {
           try {
               BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(req.getCurrency(), baseCurrency);
               convertedAmount = convertedAmount.multiply(exchangeRate);
           } catch (Exception e) {
               return new SubscriptionResponseDTO(null, "Currency conversion failed: " + e.getMessage());
           }
       }
   
       SubscriptionEntity subscription = new SubscriptionEntity();
       subscription.setUsername(username);
       subscription.setSubscriptionName(req.getSubscriptionName());
       subscription.setTransactionType(req.getTransactionType());
       subscription.setAmount(convertedAmount.doubleValue());
       subscription.setCurrency(baseCurrency);
       subscription.setRecurrencePattern(req.getRecurrencePattern());
       subscription.setStartDate(LocalDateTime.now());
       subscription.setEndDate(req.getEndDate());
       subscription.setCategory(req.getCategory()); // Set the category entity
       subscription.setLastTransactionDate(LocalDateTime.now());
   
       subscriptionRepository.save(subscription);
       return new SubscriptionResponseDTO("Subscription created successfully", null);
    }

    // Update Subscription
    @Transactional
    public SubscriptionResponseDTO updateSubscription(String subscriptionId, SubscriptionRequestDTO req,
            String userToken) {
        String username = jwtService.getUsername(userToken);
        String baseCurrency = "USD";
        BigDecimal convertedAmount = new BigDecimal(req.getAmount());

        Optional<SubscriptionEntity> existingSubscriptionOpt = subscriptionRepository.findById(subscriptionId);

        if (existingSubscriptionOpt.isEmpty()) {
            return new SubscriptionResponseDTO(null, "Subscription not found");
        }

        SubscriptionEntity existingSubscription = existingSubscriptionOpt.get();
        if (!existingSubscription.getUsername().equals(username)) {
            return new SubscriptionResponseDTO(null, "You can only update your own subscriptions");
        }

        if (!req.getCurrency().equalsIgnoreCase(baseCurrency)) {
            try {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(req.getCurrency(), baseCurrency);
                convertedAmount = convertedAmount.multiply(exchangeRate);
            } catch (Exception e) {
                return new SubscriptionResponseDTO(null, "Currency conversion failed: " + e.getMessage());
            }
        }

        existingSubscription.setSubscriptionName(req.getSubscriptionName());
        existingSubscription.setTransactionType(req.getTransactionType());
        existingSubscription.setAmount(convertedAmount.doubleValue());
        existingSubscription.setCurrency(baseCurrency);
        existingSubscription.setRecurrencePattern(req.getRecurrencePattern());
        existingSubscription.setEndDate(req.getEndDate());
        existingSubscription.setCategory(req.getCategory());

        subscriptionRepository.save(existingSubscription);
        return new SubscriptionResponseDTO("Subscription updated successfully", null);
    }

    // Delete Subscription
    @Transactional
    public SubscriptionResponseDTO deleteSubscription(String subscriptionId, String verify, String userToken) {
        String username = jwtService.getUsername(userToken);
        if (!"yes".equalsIgnoreCase(verify)) {
            return new SubscriptionResponseDTO("Type 'yes' to confirm deletion", "Verify error");
        }

        Optional<SubscriptionEntity> existingSubscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (existingSubscriptionOpt.isEmpty()) {
            return new SubscriptionResponseDTO(null, "Subscription not found");
        }

        SubscriptionEntity existingSubscription = existingSubscriptionOpt.get();
        if (!existingSubscription.getUsername().equals(username)) {
            return new SubscriptionResponseDTO(null, "You can only delete your own subscriptions");
        }

        subscriptionRepository.delete(existingSubscription);
        return new SubscriptionResponseDTO("Subscription deleted successfully", null);
    }

    // Handle Recurring Transactions
    @Transactional
    public void handleRecurringTransactions() {
        List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (SubscriptionEntity subscription : subscriptions) {
            if (subscription.getEndDate() != null && subscription.getEndDate().isBefore(now)) {
                continue;
            }

            if (shouldGenerateTransaction(subscription)) {
                sendReminderEmail(null ,subscription);
                transactionService.addRecurringTransaction(subscription);
                subscription.setLastTransactionDate(now);
                subscriptionRepository.save(subscription);
            }
        }
    }

    private boolean shouldGenerateTransaction(SubscriptionEntity subscription) {
        LocalDateTime lastTransactionDate = subscription.getLastTransactionDate();
        LocalDateTime now = LocalDateTime.now();

        switch (subscription.getRecurrencePattern().toLowerCase()) {
            case "daily":
                return lastTransactionDate.plusDays(1).isBefore(now);
            case "weekly":
                return lastTransactionDate.plusWeeks(1).isBefore(now);
            case "monthly":
                return lastTransactionDate.plusMonths(1).isBefore(now);
            default:
                return false;
        }
    }

    public void sendReminderEmail(String userEmail, SubscriptionEntity subscription) {
        String subject = "Upcoming Subscription Payment Reminder";
        String body = "Dear " + subscription.getUsername() + ",\n\n"
                + "Your subscription for '" + subscription.getSubscriptionName() + "' is due soon.\n"
                + "Amount: $" + subscription.getAmount() + " " + subscription.getCurrency() + "\n"
                + "Next Billing Date: " + LocalDateTime.now().plusDays(1) + "\n\n"
                + "Please ensure you have sufficient balance.\n\n"
                + "Thank you for using our service.";
    
        emailService.sendEmail(userEmail, subject, body);
    }
    
    
    
}
