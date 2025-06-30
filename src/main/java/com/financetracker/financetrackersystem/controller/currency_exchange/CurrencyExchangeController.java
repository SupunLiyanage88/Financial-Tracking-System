package com.financetracker.financetrackersystem.controller.currency_exchange;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.transaction.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CurrencyExchangeController {

    private final JWTService jwtService;
    private final TransactionService transactionService;
    
    @GetMapping("/exchange-my-transactions")
    public List<TransactionEntity> exchangeUserTransactions(HttpServletRequest request, @RequestParam("currency") String targetCurrency) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        return transactionService.exchangeUserTransactions(jwtToken, targetCurrency);
    }

}
