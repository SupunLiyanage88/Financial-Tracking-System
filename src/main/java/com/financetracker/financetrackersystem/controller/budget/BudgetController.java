package com.financetracker.financetrackersystem.controller.budget;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.dto.budget.BudgetRequestDTO;
import com.financetracker.financetrackersystem.dto.budget.BudgetResponseDTO;
import com.financetracker.financetrackersystem.entity.budget.BudgetEntity;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.budget.BudgetService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class BudgetController {

    private final JWTService jwtService;
    private final BudgetService budgetService;

    // Add budget
    @PostMapping("/create-budget")
    public ResponseEntity<BudgetResponseDTO> createBudget(
        @RequestBody BudgetRequestDTO req,
        HttpServletRequest request
        ){
        String jwtToken = jwtService.extractJwtFromCookie(request);
        BudgetResponseDTO res = budgetService.createBudget(req, jwtToken);
        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    //Get user budget
    @GetMapping("/get-user-budget")
    public List<BudgetEntity> getAllBudget(HttpServletRequest request) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        String username = jwtService.getUsername(jwtToken);
        return budgetService.getUserAllBudget(username);
    }

    // Get all Budget
    @GetMapping("/get-all-budgets")
    public List<BudgetEntity> getAdminAllBudget(HttpServletRequest request) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        String userRole = jwtService.getUserRole(jwtToken);
        return budgetService.getAdminAllBudget(userRole);
    }

    // Update Budget
    @PutMapping("/update-budget/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            @RequestBody BudgetRequestDTO req,
            HttpServletRequest request,
            @PathVariable String budgetId) {
        BudgetResponseDTO res = budgetService.updateBudget(req, budgetId);
        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // Delete Budget
    @DeleteMapping("/delete-budget/{budgetId}/{verify}")
    public ResponseEntity<BudgetResponseDTO> deleteBudget(
            HttpServletRequest request,
            @PathVariable String verify,
            @PathVariable String budgetId) {
        String jwt_token = jwtService.extractJwtFromCookie(request);
        BudgetResponseDTO res = budgetService.deleteBudget(jwt_token, verify, budgetId);
        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //Check budget status
    @GetMapping("/check-budget")
    public ResponseEntity<List<String>> checkBudget(HttpServletRequest request) {
        String userToken = jwtService.extractJwtFromCookie(request);

        try {
            String username = jwtService.getUsername(userToken);

            List<String> budgetMessages = budgetService.checkBudgetThresholds(username);

            if (budgetMessages.isEmpty()) {
                return ResponseEntity.ok(List.of("Your budget is under control."));
            }

            return ResponseEntity.ok(budgetMessages);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("An error occurred while checking your budget: " + e.getMessage()));
        }
    }

    //Get budget recommends
    @GetMapping("/budget-recommends")
    public String getBudgetRecommendation(HttpServletRequest request) {
        String userToken = jwtService.extractJwtFromCookie(request);
        String username = jwtService.getUsername(userToken);
        return budgetService.getBudgetRecommendation(username);
    }

}
