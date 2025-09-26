package com.stock_trading_simulation_system.dto;

import lombok.Data;
import java.time.LocalDate;

@Data // Lombok annotation
public class BacktestRequest {
    private String symbol;
    private String strategyName; // Jaise: "Simple Moving Average"
    private int periodShort;     // Short MA period (e.g., 20 days)
    private int periodLong;      // Long MA period (e.g., 50 days)
    private LocalDate startDate;
    private LocalDate endDate;
}