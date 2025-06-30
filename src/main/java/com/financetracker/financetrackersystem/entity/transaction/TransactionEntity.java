package com.financetracker.financetrackersystem.entity.transaction;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionEntity {
    @Id
    private String id;
    private String username;
    private String category;
    private Double amount;
    private String type; 
    private String currency;
    private LocalDateTime date;

}
