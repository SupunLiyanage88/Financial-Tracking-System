package com.financetracker.financetrackersystem.service.goal;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.goal.GoalRequestDTO;
import com.financetracker.financetrackersystem.dto.goal.GoalResponseDTO;
import com.financetracker.financetrackersystem.entity.goal.GoalEntity;
import com.financetracker.financetrackersystem.repository.GoalRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final JWTService jwtService;
    private final CurrencyExchangeService currencyExchangeService;

    // Create a new goal
    public GoalResponseDTO createGoal(GoalRequestDTO req, String userToken) {

        String username = jwtService.getUsername(userToken);

        //Convert transaction amount to a base currency
        String baseCurrency = "USD";
        BigDecimal convertedAmount = BigDecimal.valueOf(req.getTargetAmount());

        if (!req.getCurrency().equalsIgnoreCase(baseCurrency)) {
            try {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(req.getCurrency(), baseCurrency);
                convertedAmount = convertedAmount.multiply(exchangeRate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Currency conversion failed: ");
            }
        }

        GoalEntity goal = new GoalEntity();
        goal.setUsername(username);
        goal.setGoalName(req.getGoalName());
        goal.setTargetAmount(convertedAmount.doubleValue());
        goal.setTargetDate(req.getTargetDate());
        goal.setCurrency(baseCurrency);
        goal.setSavedAmount(0.0); 
        goal.setStatus("In Progress"); 

        GoalEntity savedGoal = goalRepository.save(goal);
        return mapToGoalResponseDTO(savedGoal);
    }

    // Get all goals for a user
    public List<GoalResponseDTO> getGoalsByUsername(String userToken) {
        String username = jwtService.getUsername(userToken);
        List<GoalEntity> goals = goalRepository.findByUsername(username);
        return goals.stream()
                .map(this::mapToGoalResponseDTO)
                .collect(Collectors.toList());
    }

    // Update progress toward a goal
    public GoalResponseDTO updateGoalProgress(String goalId, String currency, Double amountToAdd) {
        
        //Convert transaction amount to a base currency
        String baseCurrency = "USD";
        BigDecimal convertedAmount = BigDecimal.valueOf(amountToAdd);

        if (!currency.equalsIgnoreCase(baseCurrency)) {
            try {
                BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(currency, baseCurrency);
                convertedAmount = convertedAmount.multiply(exchangeRate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Currency conversion failed: ");
            }
        }
        
        GoalEntity goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setSavedAmount(goal.getSavedAmount() + convertedAmount.doubleValue());
        if (goal.getSavedAmount() >= goal.getTargetAmount()) {
            goal.setStatus("Completed");
        }
        GoalEntity updatedGoal = goalRepository.save(goal);
        return mapToGoalResponseDTO(updatedGoal);
    }

    // Delete a goal
    public void deleteGoal(String goalId) {
        goalRepository.deleteById(goalId);
    }

    // Helper method to map GoalEntity to GoalResponseDTO
    private GoalResponseDTO mapToGoalResponseDTO(GoalEntity goal) {

        GoalResponseDTO responseDTO = new GoalResponseDTO();
        responseDTO.setGoalName(goal.getGoalName());
        responseDTO.setTargetAmount(goal.getTargetAmount());
        responseDTO.setSavedAmount(goal.getSavedAmount());
        responseDTO.setTargetDate(goal.getTargetDate());
        responseDTO.setCurrency(goal.getCurrency());
        responseDTO.setStatus(goal.getStatus());
        return responseDTO;
    }
}
