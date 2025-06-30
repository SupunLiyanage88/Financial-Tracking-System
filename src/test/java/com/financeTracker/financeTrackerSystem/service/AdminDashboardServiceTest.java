package com.financeTracker.financeTrackerSystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.repository.UserRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.dashboard.AdminDashboardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    private String validJwtToken;
    private String invalidJwtToken;

    @BeforeEach
    public void setUp() {
        validJwtToken = "valid.jwt.token";
        invalidJwtToken = "invalid.jwt.token";
    }

    @Test
    public void testGetAllUsers_AdminRole() {
        // Arrange
        when(jwtService.getUserRole(validJwtToken)).thenReturn("Admin");
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        List<UserEntity> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserEntity> result = adminDashboardService.getAllUsers(validJwtToken);

        // Assert
        assertEquals(2, result.size());
        verify(jwtService, times(1)).getUserRole(validJwtToken);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllUsers_NonAdminRole() {
        // Arrange
        when(jwtService.getUserRole(invalidJwtToken)).thenReturn("User");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminDashboardService.getAllUsers(invalidJwtToken);
        });

        assertEquals("You are not authorized to access this resource", exception.getMessage());
        verify(jwtService, times(1)).getUserRole(invalidJwtToken);
        verify(userRepository, never()).findAll();
    }

    @Test
    public void testGetTotalUsersCount_AdminRole() {
        // Arrange
        when(jwtService.getUserRole(validJwtToken)).thenReturn("Admin");
        when(userRepository.count()).thenReturn(10L);

        // Act
        long result = adminDashboardService.getTotalUsersCount(validJwtToken);

        // Assert
        assertEquals(10L, result);
        verify(jwtService, times(1)).getUserRole(validJwtToken);
        verify(userRepository, times(1)).count();
    }

    @Test
    public void testGetTotalUsersCount_NonAdminRole() {
        // Arrange
        when(jwtService.getUserRole(invalidJwtToken)).thenReturn("User");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminDashboardService.getTotalUsersCount(invalidJwtToken);
        });

        assertEquals("You are not authorized to access this resource", exception.getMessage());
        verify(jwtService, times(1)).getUserRole(invalidJwtToken);
        verify(userRepository, never()).count();
    }

    @Test
    public void testGetAllTransactions_AdminRole() {
        // Arrange
        when(jwtService.getUserRole(validJwtToken)).thenReturn("Admin");
        TransactionEntity transaction1 = new TransactionEntity();
        TransactionEntity transaction2 = new TransactionEntity();
        List<TransactionEntity> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<TransactionEntity> result = adminDashboardService.getAllTransactions(validJwtToken);

        // Assert
        assertEquals(2, result.size());
        verify(jwtService, times(1)).getUserRole(validJwtToken);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllTransactions_NonAdminRole() {
        // Arrange
        when(jwtService.getUserRole(invalidJwtToken)).thenReturn("User");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminDashboardService.getAllTransactions(invalidJwtToken);
        });

        assertEquals("You are not authorized to access this resource", exception.getMessage());
        verify(jwtService, times(1)).getUserRole(invalidJwtToken);
        verify(transactionRepository, never()).findAll();
    }

}