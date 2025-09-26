package com.stock_trading_simulation_system.controller;

import com.stock_trading_simulation_system.model.Portfolio;
import com.stock_trading_simulation_system.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Portfolio>> getUserPortfolio(@PathVariable Long userId) {
        List<Portfolio> portfolio = portfolioService.findByUserId(userId);
        
        // Naya Logic:
        // Agar portfolio khaali hai, to bhi 404 (Not Found) ki jagah 200 (OK)
        // status ke saath khaali list ([]) bhejenge.
        // Frontend isse 'No stocks' dikhakar handle kar lega.
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }
}