package com.financetracker.financetrackersystem.entity.goal;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "goals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoalEntity {
    @Id
    private String id;
    private String username; // User associated with the goal
    private String goalName; // Name of the goal (e.g., "Save for a car")
    private Double targetAmount; // Target amount to save
    private Double savedAmount; // Amount saved so far
    private LocalDate targetDate;// Target date to achieve the goal
    private String currency; 
    private String status; // Status of the goal (e.g., "In Progress", "Completed")
}