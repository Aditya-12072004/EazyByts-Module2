package com.stock_trading_simulation_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class MarketDataService {

    private final WebClient webClient;

    @Value("${alphavantage.api.key}") // Alpha Vantage Key
    private String apiKey;

    @Value("${alphavantage.api.base-url}") // Alpha Vantage Base URL
    private String baseUrl;

    public MarketDataService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Yeh method Alpha Vantage API se kisi bhi stock ka live price fetch karti hai (GLOBAL_QUOTE).
     */
    public BigDecimal getLivePrice(String stockSymbol) {
        try {
            // URL: GLOBAL_QUOTE function ka use karein
            String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                baseUrl, stockSymbol, apiKey);

            // API call karein aur response ko Map mein store karein
            Map<String, Object> response = webClient.get()
                .uri(url)
                .retrieve()
                
                // Status Handler: 4xx/5xx errors ko handle karein
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                     return clientResponse.bodyToMono(String.class)
                         .map(error -> new RuntimeException("Alpha Vantage API Error: Check API key or stock symbol."));
                })
                
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

            // Data extraction logic Alpha Vantage ke hisaab se
            if (response != null && response.containsKey("Global Quote")) {
                // Type Safety Fix: @SuppressWarnings ka use kiya gaya hai
                @SuppressWarnings("unchecked")
                Map<String, String> globalQuote = (Map<String, String>) response.get("Global Quote");
                
                // Live Price '05. price' key mein hota hai
                String priceString = globalQuote.get("05. price"); 
                
                if (priceString != null && !priceString.isEmpty()) {
                    return new BigDecimal(priceString);
                }
            }
            
            // Agar Global Quote ya price nahi mila
            throw new RuntimeException("Could not fetch price for symbol: " + stockSymbol + ". (Check symbol or API limit)");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch live stock data: " + e.getMessage());
        }
    }

    /**
     * Naya: Yeh method Alpha Vantage API se historical data fetch karti hai (TIME_SERIES_DAILY).
     */
    public String getHistoricalData(String stockSymbol, String resolution, long from, long to) {
        // Note: Alpha Vantage Unix Timestamps ki jagah seedhi dates (YYYY-MM-DD) use karta hai.
        
        try {
            // Historical URL: TIME_SERIES_DAILY_ADJUSTED function ka use karein
            String url = String.format("%s?function=TIME_SERIES_DAILY_ADJUSTED&symbol=%s&outputsize=full&apikey=%s",
                baseUrl, stockSymbol, apiKey);

            String response = webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                     return clientResponse.bodyToMono(String.class)
                         .map(error -> new RuntimeException("Alpha Vantage Historical API Error."));
                })
                .bodyToMono(String.class)
                .block();

            // Alpha Vantage 'Error Message' bhejta hai agar koi galti ho
            if (response == null || response.contains("Error Message")) {
                throw new RuntimeException("No historical data found. (API Limit or invalid symbol)");
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch historical data: " + e.getMessage());
        }
    }
}