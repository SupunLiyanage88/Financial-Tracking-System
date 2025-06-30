package com.financetracker.financetrackersystem.dto.transaction;

import java.time.LocalDateTime;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionRequestDTO {
    private String category;
    private Double amount;
    private String type;
    private String currency;
    private LocalDateTime date;

}
