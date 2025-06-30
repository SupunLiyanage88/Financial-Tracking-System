package com.financetracker.financetrackersystem.dto.transaction;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResponseDTO {

    private String message;
    private String error;

}
