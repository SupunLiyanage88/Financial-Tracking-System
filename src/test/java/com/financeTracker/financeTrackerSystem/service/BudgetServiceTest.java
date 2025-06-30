package com.financeTracker.financeTrackerSystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.financetracker.financetrackersystem.dto.budget.BudgetRequestDTO;
import com.financetracker.financetrackersystem.dto.budget.BudgetResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.entity.budget.BudgetEntity;
import com.financetracker.financetrackersystem.repository.BudgetRepository;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.budget.BudgetService;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private JWTService jwtService;
    
    @InjectMocks
    private BudgetService budgetService;

    private BudgetRequestDTO budgetRequestDTO;
    private BudgetEntity budgetEntity;

    @BeforeEach
    void setUp() {
        budgetRequestDTO = new BudgetRequestDTO();
        budgetRequestDTO.setAmount(1000.0);
        budgetRequestDTO.setCurrency("USD");
        budgetRequestDTO.setCategory("Groceries");
        budgetRequestDTO.setStartDate(LocalDateTime.now());
        budgetRequestDTO.setEndDate(LocalDateTime.now().plusMonths(1));

        budgetEntity = new BudgetEntity();
        budgetEntity.setId("1");
        budgetEntity.setUsername("user1");
        budgetEntity.setAmount(1000.0);
        budgetEntity.setCategory("Groceries");
        budgetEntity.setCurrency("USD");
        budgetEntity.setStartDate(LocalDateTime.now());
        budgetEntity.setEndDate(LocalDateTime.now().plusMonths(1));
    }

    @Test
    void testCreateBudget_Success() {
        when(jwtService.getUsername("validToken")).thenReturn("user1"); // Stubbing for JWTService
        lenient().when(currencyExchangeService.getExchangeRate("USD", "USD")).thenReturn(BigDecimal.ONE); // Lenient stubbing for CurrencyExchangeService
        when(budgetRepository.save(any(BudgetEntity.class))).thenReturn(budgetEntity); // Stubbing for BudgetRepository
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new CategoryEntity()));
    
        BudgetResponseDTO response = budgetService.createBudget(budgetRequestDTO, "validToken");
    
        assertNotNull(response);
        assertEquals("Budget created successfully", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testCreateBudget_InvalidCurrency() {
        budgetRequestDTO.setCurrency("INVALID");

        BudgetResponseDTO response = budgetService.createBudget(budgetRequestDTO, "validToken");

        assertNotNull(response);
        assertEquals("Invalid currency code. Must be a valid ISO 4217 code (e.g., USD, EUR, LKR).", response.getError());
        assertNull(response.getMessage());
    }

    @Test
    void testGetUserAllBudget() {
        when(budgetRepository.findByUsername("user1")).thenReturn(Collections.singletonList(budgetEntity));

        List<BudgetEntity> budgets = budgetService.getUserAllBudget("user1");

        assertNotNull(budgets);
        assertEquals(1, budgets.size());
        assertEquals("user1", budgets.get(0).getUsername());
    }

    @Test
    void testUpdateBudget_Success() {
        when(budgetRepository.findById("1")).thenReturn(Optional.of(budgetEntity));
        when(budgetRepository.save(any(BudgetEntity.class))).thenReturn(budgetEntity);
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new CategoryEntity()));

        BudgetResponseDTO response = budgetService.updateBudget(budgetRequestDTO, "1");

        assertNotNull(response);
        assertEquals("Budget updated successfully, changes saved.", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testUpdateBudget_NotFound() {
        when(budgetRepository.findById("1")).thenReturn(Optional.empty());

        BudgetResponseDTO response = budgetService.updateBudget(budgetRequestDTO, "1");

        assertNotNull(response);
        assertEquals("Unable to find Budget", response.getError());
        assertNull(response.getMessage());
    }

    @Test
    void testDeleteBudget_Success() {
        when(jwtService.getUsername("validToken")).thenReturn("user1");
        when(budgetRepository.findById("1")).thenReturn(Optional.of(budgetEntity));

        BudgetResponseDTO response = budgetService.deleteBudget("validToken", "yes", "1");

        assertNotNull(response);
        assertEquals("Budget deleted successfully.", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testDeleteBudget_Unauthorized() {
        when(jwtService.getUsername("validToken")).thenReturn("user2");
        when(budgetRepository.findById("1")).thenReturn(Optional.of(budgetEntity));

        BudgetResponseDTO response = budgetService.deleteBudget("validToken", "yes", "1");

        assertNotNull(response);
        assertEquals("You are not authorized to delete this transaction.", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testGetBudgetRecommendation() {
        when(budgetRepository.findByUsername("user1")).thenReturn(Collections.singletonList(budgetEntity));
        when(transactionRepository.findByUsername("user1")).thenReturn(Collections.emptyList());

        String recommendation = budgetService.getBudgetRecommendation("user1");

        assertNotNull(recommendation);
        assertEquals("Your spending is within budget. Keep up the good work!", recommendation);
    }
}