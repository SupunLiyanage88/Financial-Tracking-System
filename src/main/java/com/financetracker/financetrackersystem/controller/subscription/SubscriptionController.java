package com.financetracker.financetrackersystem.controller.subscription;

import com.financetracker.financetrackersystem.dto.subscription.SubscriptionRequestDTO;
import com.financetracker.financetrackersystem.dto.subscription.SubscriptionResponseDTO;
import com.financetracker.financetrackersystem.entity.subscription.SubscriptionEntity;
import com.financetracker.financetrackersystem.repository.SubscriptionRepository;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.subscription.SubscriptionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SubscriptionController {

    private final JWTService jwtService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;

    // Add Subscription
    @PostMapping("/add-subscription")
    public ResponseEntity<SubscriptionResponseDTO> addSubscription(
        @RequestBody SubscriptionRequestDTO req,
        HttpServletRequest request
    ) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        SubscriptionResponseDTO res = subscriptionService.addSubscription(req, jwtToken);

        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // Update Subscription
    @PutMapping("/update-subscription/{subscriptionId}")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscription(
        @PathVariable String subscriptionId,
        @RequestBody SubscriptionRequestDTO req,
        HttpServletRequest request
    ) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        SubscriptionResponseDTO res = subscriptionService.updateSubscription(subscriptionId, req, jwtToken);

        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // Delete Subscription
    @DeleteMapping("/delete-subscription/{subscriptionId}/{verify}")
    public ResponseEntity<SubscriptionResponseDTO> deleteSubscription(
        @PathVariable String subscriptionId,
        @PathVariable String verify,
        HttpServletRequest request
    ) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        SubscriptionResponseDTO res = subscriptionService.deleteSubscription(subscriptionId, verify, jwtToken);

        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/test-send-email/{subscriptionId}")
    public ResponseEntity<String> testSendEmail(HttpServletRequest req, @PathVariable String subscriptionId) {
        // Extract JWT token from the cookie and get the user email
        String jwtToken = jwtService.extractJwtFromCookie(req);
        String userEmail = jwtService.getEmail(jwtToken);
    
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing email from token.");
        }
    
        // Check if the subscription exists
        Optional<SubscriptionEntity> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Subscription not found");
        }
    
        // Directly send the reminder email
        SubscriptionEntity subscription = subscriptionOpt.get();
        subscriptionService.sendReminderEmail(userEmail, subscription); 
    
        return ResponseEntity.ok("Test email sent successfully.");
    }
    
}
