package com.financetracker.financetrackersystem.controller.dashboard;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.financetracker.financetrackersystem.dto.DeleteUserResponseDTO;
import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.dashboard.AdminDashboardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final JWTService jwtService;

    // Get all users (for admin)
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers(HttpServletRequest request) {
        try {
            String jwtToken = jwtService.extractJwtFromCookie(request);
            List<UserEntity> users = adminDashboardService.getAllUsers(jwtToken);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching users", e);
        }
    }
    

    

    // Get all transactions (for admin)
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEntity>> getAllTransactions(HttpServletRequest request) {
        try {
            String jwtToken = jwtService.extractJwtFromCookie(request);
            List<TransactionEntity> transactions = adminDashboardService.getAllTransactions(jwtToken);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching transactions", e);
        }
    }

    // Delete a user (for admin)
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<DeleteUserResponseDTO> deleteUserByAdmin(
            @PathVariable String userId,
            HttpServletRequest request
    ) {
        try {
            String jwtToken = jwtService.extractJwtFromCookie(request);
            DeleteUserResponseDTO res = adminDashboardService.deleteUserByAdmin(userId, jwtToken);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user", e);
        }
    }

}
