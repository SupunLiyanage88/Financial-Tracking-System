package com.financeTracker.financeTrackerSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.financetracker.financetrackersystem.dto.report.ReportRequestDTO;
import com.financetracker.financetrackersystem.dto.report.ReportResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.SubscriptionRepository;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.report.ReportService;

public class ReportServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateReport() {
        // Arrange
        String userToken = "testToken";
        String username = "testUser";
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0); // Use LocalDateTime
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59); // Use LocalDateTime

        ReportRequestDTO request = new ReportRequestDTO();
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        // Mock transactions
        TransactionEntity incomeTransaction = new TransactionEntity();
        incomeTransaction.setType("income");
        incomeTransaction.setAmount(1000.0);
        incomeTransaction.setCategory("1");

        TransactionEntity expenseTransaction = new TransactionEntity();
        expenseTransaction.setType("expense");
        expenseTransaction.setAmount(500.0);
        expenseTransaction.setCategory("2");

        List<TransactionEntity> transactions = Arrays.asList(incomeTransaction, expenseTransaction);

        when(transactionRepository.findByUsernameAndDateBetween(username, startDate, endDate))
            .thenReturn(transactions);

        // Mock subscriptions
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setSubscriptionName("Netflix");
        subscription.setAmount(15.0);

        List<SubscriptionEntity> subscriptions = Collections.singletonList(subscription);

        when(subscriptionRepository.findByUsernameAndStartDateBetween(username, startDate, endDate))
            .thenReturn(subscriptions);

        // Mock categories
        CategoryEntity incomeCategory = new CategoryEntity();
        incomeCategory.setId("1");
        incomeCategory.setCategoryName("Salary");

        CategoryEntity expenseCategory = new CategoryEntity();
        expenseCategory.setId("2");
        expenseCategory.setCategoryName("Groceries");

        List<CategoryEntity> categories = Arrays.asList(incomeCategory, expenseCategory);

        when(categoryRepository.findAll()).thenReturn(categories);

        // Mock JWT service
        when(jwtService.getUsername(userToken)).thenReturn(username);

        // Act
        ReportResponseDTO response = reportService.generateReport(request, userToken);

        // Assert
        assertEquals(BigDecimal.valueOf(1000.0), response.getTotalIncome());
        assertEquals(BigDecimal.valueOf(500.0), response.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(500.0), response.getNetBalance());

        Map<String, BigDecimal> expectedCategoryWiseIncome = new HashMap<>();
        expectedCategoryWiseIncome.put("Salary", BigDecimal.valueOf(1000.0));
        assertEquals(expectedCategoryWiseIncome, response.getCategoryWiseIncome());

        Map<String, BigDecimal> expectedCategoryWiseExpenses = new HashMap<>();
        expectedCategoryWiseExpenses.put("Groceries", BigDecimal.valueOf(500.0));
        assertEquals(expectedCategoryWiseExpenses, response.getCategoryWiseExpenses());

        assertEquals(BigDecimal.valueOf(15.0), response.getTotalSubscriptionExpenses());

        Map<String, BigDecimal> expectedSubscriptionExpenses = new HashMap<>();
        expectedSubscriptionExpenses.put("Netflix", BigDecimal.valueOf(15.0));
        assertEquals(expectedSubscriptionExpenses, response.getSubscriptionExpensesByName());

        // Verify interactions
        verify(jwtService, times(1)).getUsername(userToken);
        verify(transactionRepository, times(1)).findByUsernameAndDateBetween(username, startDate, endDate);
        verify(subscriptionRepository, times(1)).findByUsernameAndStartDateBetween(username, startDate, endDate);
        verify(categoryRepository, times(1)).findAll();
    }
}