package com.stock_trading_simulation_system.controller;

import com.stock_trading_simulation_system.service.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
public class MarketDataController {

    @Autowired
    private MarketDataService marketDataService;

    @GetMapping("/quote")
    public ResponseEntity<Map<String, BigDecimal>> getStockQuote(@RequestParam String symbol) {
        try {
            BigDecimal price = marketDataService.getLivePrice(symbol);
            // Ek Map return karein, jismein 'price' key ke saath value ho
            return ResponseEntity.ok(Collections.singletonMap("price", price));
        } catch (Exception e) {
            // Agar koi error ho, to 400 Bad Request ke saath error message bhej dein
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", null));
        }
    }
}