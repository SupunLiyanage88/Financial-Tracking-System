package com.financetracker.financetrackersystem.service.dashboard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.DeleteUserResponseDTO;
import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.repository.TransactionRepository;
import com.financetracker.financetrackersystem.repository.UserRepository;
import com.financetracker.financetrackersystem.service.JWTService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final JWTService jwtService;

    // Get all users
    public List<UserEntity> getAllUsers(String jwtToken) {
        if (!jwtService.getUserRole(jwtToken).equals("Admin")) {
            throw new IllegalArgumentException("You are not authorized to access this resource");
        }
        return userRepository.findAll();
    }

    public long getTotalUsersCount(String jwtToken) {
    if (!jwtService.getUserRole(jwtToken).equals("Admin")) {
        throw new IllegalArgumentException("You are not authorized to access this resource");
    }
    return userRepository.count();
}

    // Get all transactions
    public List<TransactionEntity> getAllTransactions(String jwtToken) {
        if (!jwtService.getUserRole(jwtToken).equals("Admin")) {
            throw new IllegalArgumentException("You are not authorized to access this resource");
        }
        return transactionRepository.findAll();
    }

    // Delete a user by admin
    public DeleteUserResponseDTO deleteUserByAdmin(String userId, String jwtToken) {
        if (!jwtService.getUserRole(jwtToken).equals("Admin")) {
            return new DeleteUserResponseDTO("You are not authorized to delete this user.", null);
        }

        userRepository.deleteById(userId);
        return new DeleteUserResponseDTO("User deleted successfully.", null);
    }
}


