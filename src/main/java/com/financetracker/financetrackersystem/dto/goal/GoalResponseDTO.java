package com.financetracker.financetrackersystem.dto.goal;

import java.time.LocalDate;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoalResponseDTO {

    private String id;
    private String goalName; 
    private Double targetAmount; 
    private Double savedAmount; 
    private LocalDate targetDate;
    private String currency; 
    private String status; //"In Progress", "Completed"

}
