package com.financeTracker.financeTrackerSystem.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.financetrackersystem.service.currency_exchange.CurrencyExchangeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeServiceTest {

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private HttpURLConnection mockConnection;

    @SuppressWarnings("unused")
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetExchangeRate_SameCurrency_ShouldReturnOne() {
        BigDecimal result = currencyExchangeService.getExchangeRate("USD", "USD");
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    void testGetExchangeRate_NullOrEmptyCurrency_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> currencyExchangeService.getExchangeRate(null, "USD"));
        assertThrows(IllegalArgumentException.class, () -> currencyExchangeService.getExchangeRate("", "USD"));
        assertThrows(IllegalArgumentException.class, () -> currencyExchangeService.getExchangeRate("USD", null));
        assertThrows(IllegalArgumentException.class, () -> currencyExchangeService.getExchangeRate("USD", ""));
    }
    
    @Test
    void testGetExchangeRate_InvalidCurrencyCode_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                currencyExchangeService.getExchangeRate("INVALID", "USD"));
        assertTrue(exception.getMessage().contains("Invalid currency code"));
    }
}

