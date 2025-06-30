package com.financetracker.financetrackersystem.dto.goal;

import java.time.LocalDate;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoalRequestDTO {

    private String goalName; // Name of the goal (e.g., "Save for a car")
    private Double targetAmount; // Target amount to save
    private LocalDate targetDate; // Target date to achieve the goal
    private String currency;

}
