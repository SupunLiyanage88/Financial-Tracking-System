package com.financetracker.financetrackersystem.controller.transaction;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.dto.transaction.TransactionRequestDTO;
import com.financetracker.financetrackersystem.dto.transaction.TransactionResponseDTO;
import com.financetracker.financetrackersystem.entity.transaction.TransactionEntity;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.transaction.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TransactionController {

    private final JWTService jwtService;
    private final TransactionService transactionService;

    //Add Transaction
    @PostMapping("/add-user-transaction")
    public ResponseEntity<TransactionResponseDTO> addTransaction(
        @RequestBody TransactionRequestDTO req, 
        HttpServletRequest request
        ){
        String jwtToken = jwtService.extractJwtFromCookie(request);
        TransactionResponseDTO res = transactionService.addTransaction(req, jwtToken);
        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    
    //Get user transaction
    @GetMapping("/get-my-transactions")
    public List<TransactionEntity> getUserAllTransaction(HttpServletRequest request) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        return transactionService.getUserAllTransaction(jwtToken);
    }    

    //Get all transactions for admin
    @GetMapping("/get-all-transactions")
    public List<TransactionEntity> getAllTransaction(HttpServletRequest request) {
        String jetToken =jwtService.extractJwtFromCookie(request);
        return transactionService.getAllTransaction(jetToken);
    }

    //Update transaction
    @PutMapping("/update-transaction/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
        @RequestBody TransactionRequestDTO req, 
        HttpServletRequest request,
        @PathVariable String transactionId
        ){
        TransactionResponseDTO res = transactionService.updateTransaction(req, transactionId);
        if("Unauthorized".equals(res.getError())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }else if(res.getError()!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //Delete transaction
    @DeleteMapping("/delete-transaction/{transactionId}/{verify}")
    public ResponseEntity<TransactionResponseDTO> deleteTransaction(
        HttpServletRequest request,
        @PathVariable String verify,
        @PathVariable String transactionId
    ){
        String jwt_token = jwtService.extractJwtFromCookie(request);
        TransactionResponseDTO res = transactionService.deleteTransaction(jwt_token, verify, transactionId);
        if("Unauthorized".equals(res.getError())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }else if(res.getError()!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    

}
