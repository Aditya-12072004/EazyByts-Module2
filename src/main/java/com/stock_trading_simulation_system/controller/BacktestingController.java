package com.stock_trading_simulation_system.controller;

import com.stock_trading_simulation_system.dto.BacktestRequest;
import com.stock_trading_simulation_system.service.BacktestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/backtest")
public class BacktestingController {

    @Autowired
    private BacktestingService backtestingService;

    /**
     * Handles POST request from the frontend to run a trading strategy backtest.
     * Request body contains parameters like symbol, start/end date, and MA periods.
     */
    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runBacktest(@RequestBody BacktestRequest request) {
        try {
            // Service layer ko call karein jahan actual logic run hota hai
            Map<String, Object> result = backtestingService.runStrategy(request);
            
            // Agar sab theek hai, to 200 OK ke saath result bhej dein
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // Agar service layer se koi runtime exception (jaise 'No data found') aati hai,
            // to 400 Bad Request ke saath error message bhej dein
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
}