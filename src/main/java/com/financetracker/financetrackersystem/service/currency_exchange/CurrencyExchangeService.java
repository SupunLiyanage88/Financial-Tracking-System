package com.financetracker.financetrackersystem.service.currency_exchange;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CurrencyExchangeService {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD"; 

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null || fromCurrency.isEmpty() || toCurrency.isEmpty()) {
            throw new IllegalArgumentException("Currency codes cannot be null or empty");
        }

        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE; 
        }

        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IllegalArgumentException("Failed to fetch exchange rates. HTTP Code: " + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());

            // Validate JSON response
            if (!jsonResponse.has("rates")) {
                throw new IllegalArgumentException("Invalid API response: Missing 'rates' key");
            }

            JsonNode rates = jsonResponse.get("rates");
            if (!rates.has(fromCurrency.toUpperCase()) || !rates.has(toCurrency.toUpperCase())) {
                throw new IllegalArgumentException("Invalid currency code. Make sure it's an ISO 4217 code.");
            }

            BigDecimal fromRate = rates.get(fromCurrency.toUpperCase()).decimalValue();
            BigDecimal toRate = rates.get(toCurrency.toUpperCase()).decimalValue();

            // Return exchange rate with proper rounding
            return toRate.divide(fromRate, 4, RoundingMode.HALF_UP);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error fetching exchange rates: " + e.getMessage(), e);
        }
    }
}
