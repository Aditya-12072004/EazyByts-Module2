package com.stock_trading_simulation_system.controller;

import com.stock_trading_simulation_system.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/trading")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    // Is method mein ab 'price' nahi liya jaa raha
    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(@RequestBody Map<String, Object> tradingDetails) {
        try {
            Long userId = Long.parseLong(tradingDetails.get("userId").toString());
            String stockSymbol = (String) tradingDetails.get("stockSymbol");
            int quantity = (Integer) tradingDetails.get("quantity");

            // TradingService ki method ko ab sirf 3 arguments diye jaa rahe hain
            tradingService.buyStock(userId, stockSymbol, quantity);
            return new ResponseEntity<>("Stock successfully bought", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Is method mein ab 'price' nahi liya jaa raha
    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestBody Map<String, Object> tradingDetails) {
        try {
            Long userId = Long.parseLong(tradingDetails.get("userId").toString());
            String stockSymbol = (String) tradingDetails.get("stockSymbol");
            int quantity = (Integer) tradingDetails.get("quantity");

            // TradingService ki method ko ab sirf 3 arguments diye jaa rahe hain
            tradingService.sellStock(userId, stockSymbol, quantity);
            return new ResponseEntity<>("Stock successfully sold", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}