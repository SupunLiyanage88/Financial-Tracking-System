package com.financetracker.financetrackersystem.controller.goal;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.dto.goal.GoalRequestDTO;
import com.financetracker.financetrackersystem.dto.goal.GoalResponseDTO;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.goal.GoalService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final JWTService jwtService;

    @PostMapping("/create-goal")
    public GoalResponseDTO createGoal(@RequestBody GoalRequestDTO req, HttpServletRequest request ) {
        String userToken = jwtService.extractJwtFromCookie(request);
        return goalService.createGoal(req, userToken);
    }

    @GetMapping("/get-goals")
    public List<GoalResponseDTO> getGoalsByUsername(HttpServletRequest req) {
        String userToken = jwtService.extractJwtFromCookie(req);
        return goalService.getGoalsByUsername(userToken);
    }

    @PutMapping("/update-goal/{goalId}")
    public GoalResponseDTO updateGoalProgress(@PathVariable String goalId,@RequestParam String currency, @RequestParam Double amountToAdd) {
        return goalService.updateGoalProgress(goalId, currency, amountToAdd);
    }

    @DeleteMapping("/delete/{goalId}")
    public void deleteGoal(@PathVariable String goalId) {
        goalService.deleteGoal(goalId);
    }

}
