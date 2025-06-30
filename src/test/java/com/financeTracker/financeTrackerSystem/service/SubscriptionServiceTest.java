package com.financeTracker.financeTrackerSystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.financetracker.financetrackersystem.dto.subscription.SubscriptionRequestDTO;
import com.financetracker.financetrackersystem.dto.subscription.SubscriptionResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.SubscriptionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;
import com.financetracker.financetrackersystem.service.email.EmailService;
import com.financetracker.financetrackersystem.service.subscription.SubscriptionService;
import com.financetracker.financetrackersystem.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private JWTService jwtService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddSubscription_Success() {
        // Arrange
        SubscriptionRequestDTO request = new SubscriptionRequestDTO();
        request.setSubscriptionName("Netflix");
        request.setTransactionType("Expense");
        request.setAmount((double) 10);
        request.setCurrency("USD");
        request.setRecurrencePattern("Monthly");
        request.setEndDate(LocalDateTime.now().plusMonths(1));
        request.setCategory("67d5d63f62fde0348da16095");

        when(jwtService.getUsername("validToken")).thenReturn("user1");
        when(currencyExchangeService.getExchangeRate("USD", "USD")).thenReturn(BigDecimal.ONE);
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(new SubscriptionEntity());
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new CategoryEntity()));

        // Act
        SubscriptionResponseDTO response = subscriptionService.addSubscription(request, "validToken");

        // Assert
        assertNotNull(response);
        assertEquals("Subscription created successfully", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testAddSubscription_CurrencyConversionFailure() {
        // Arrange
        SubscriptionRequestDTO request = new SubscriptionRequestDTO();
        request.setSubscriptionName("Netflix");
        request.setTransactionType("Expense");
        request.setAmount((double) 10);
        request.setCurrency("EUR");
        request.setRecurrencePattern("Monthly");
        request.setEndDate(LocalDateTime.now().plusMonths(1));
        request.setCategory("Entertainment");

        when(jwtService.getUsername("validToken")).thenReturn("user1");
        when(currencyExchangeService.getExchangeRate("EUR", "USD")).thenThrow(new RuntimeException("Conversion failed"));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new CategoryEntity()));

        // Act
        SubscriptionResponseDTO response = subscriptionService.addSubscription(request, "validToken");

        // Assert
        assertNotNull(response);
        assertNull(response.getMessage());
        assertTrue(response.getError().contains("Currency conversion failed"));
    }

    @Test
    void testUpdateSubscription_Success() {
        // Arrange
        String subscriptionId = "sub1";
        SubscriptionRequestDTO request = new SubscriptionRequestDTO();
        request.setSubscriptionName("Netflix Updated");
        request.setTransactionType("Expense");
        request.setAmount((double) 15);
        request.setCurrency("USD");
        request.setRecurrencePattern("Monthly");
        request.setEndDate(LocalDateTime.now().plusMonths(1));
        request.setCategory("Entertainment");

        SubscriptionEntity existingSubscription = new SubscriptionEntity();
        existingSubscription.setUsername("user1");

        when(jwtService.getUsername("validToken")).thenReturn("user1");
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existingSubscription));
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenReturn(existingSubscription);

        // Act
        SubscriptionResponseDTO response = subscriptionService.updateSubscription(subscriptionId, request, "validToken");

        // Assert
        assertNotNull(response);
        assertEquals("Subscription updated successfully", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testDeleteSubscription_Success() {
        // Arrange
        String subscriptionId = "sub1";
        SubscriptionEntity existingSubscription = new SubscriptionEntity();
        existingSubscription.setUsername("user1");

        when(jwtService.getUsername("validToken")).thenReturn("user1");
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(existingSubscription));

        // Act
        SubscriptionResponseDTO response = subscriptionService.deleteSubscription(subscriptionId, "yes", "validToken");

        // Assert
        assertNotNull(response);
        assertEquals("Subscription deleted successfully", response.getMessage());
        assertNull(response.getError());
        verify(subscriptionRepository, times(1)).delete(existingSubscription);
    }

    @Test
    void testDeleteSubscription_NotFound() {
        // Arrange
        String subscriptionId = "sub1";

        when(jwtService.getUsername("validToken")).thenReturn("user1");
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        // Act
        SubscriptionResponseDTO response = subscriptionService.deleteSubscription(subscriptionId, "yes", "validToken");

        // Assert
        assertNotNull(response);
        assertNull(response.getMessage());
        assertEquals("Subscription not found", response.getError());
    }

    @Test
    void testHandleRecurringTransactions() {
        // Arrange
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setRecurrencePattern("Monthly");
        subscription.setLastTransactionDate(LocalDateTime.now().minusMonths(1));
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));

        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        // Act
        subscriptionService.handleRecurringTransactions();

        // Assert
        verify(transactionService, times(1)).addRecurringTransaction(subscription);
        verify(subscriptionRepository, times(1)).save(subscription);
    }
}