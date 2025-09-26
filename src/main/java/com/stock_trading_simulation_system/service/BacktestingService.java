package com.stock_trading_simulation_system.service;

import com.fasterxml.jackson.core.type.TypeReference; // Naya Import
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock_trading_simulation_system.dto.BacktestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Map;

@Service
public class BacktestingService {

    @Autowired
    private MarketDataService marketDataService;

    // Timeframe set karein: 'D' for Daily data
    private final String RESOLUTION = "D";

    /**
     * Yeh method user ki di hui strategy ko historical data par run karti hai.
     */
    public Map<String, Object> runStrategy(BacktestRequest request) {
        
        // 1. Dates ko Unix Timestamp (seconds) mein convert karein
        long from = request.getStartDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long to = request.getEndDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        // 2. MarketDataService se Historical data fetch karein
        String rawData = marketDataService.getHistoricalData(
            request.getSymbol(), RESOLUTION, from, to
        );

        // 3. Raw JSON string ko Java Map mein parse karein
        Map<String, Object> historyData;
        try {
            // ObjectMapper ko TypeReference ka use karke batayein ki hum Map<String, Object> expect kar rahe hain
            historyData = new ObjectMapper().readValue(rawData, new TypeReference<Map<String, Object>>() {}); // <-- Yeh line theek ki gayi
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse historical data: " + e.getMessage());
        }

        // 4. Strategy Logic Apply Karen
        // Hum yahan sirf SMA_CROSSOVER ke liye ek dummy result de rahe hain.
        if ("SMA_CROSSOVER".equals(request.getStrategyName())) {
            
            // NOTE: Yahaan par asal backtesting logic aayega.
            
            // Final result ko map mein add karein
            historyData.put("backtestResult", "SMA Crossover strategy analyzed successfully.");
            historyData.put("profit_percent", 15.5); // Dummy Profit Percentage
            historyData.put("short_period", request.getPeriodShort());
            historyData.put("long_period", request.getPeriodLong());
        } else {
            historyData.put("backtestResult", "Strategy " + request.getStrategyName() + " is not yet implemented.");
        }
        
        return historyData;
    }
}