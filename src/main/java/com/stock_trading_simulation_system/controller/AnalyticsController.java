package com.stock_trading_simulation_system.controller;

import com.stock_trading_simulation_system.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/pnl/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getProfitLoss(@PathVariable Long userId) {
        Map<String, BigDecimal> pnl = analyticsService.calculateProfitLoss(userId);
        return ResponseEntity.ok(pnl);
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<com.stock_trading_simulation_system.model.Transaction>> getTransactionHistory(@PathVariable Long userId) {
        List<com.stock_trading_simulation_system.model.Transaction> history = analyticsService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(history);
    }
}