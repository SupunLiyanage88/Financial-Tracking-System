package com.financetracker.financetrackersystem.dto.report;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportRequestDTO {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String category;
}
