package com.stock_trading_simulation_system.service;

import com.stock_trading_simulation_system.model.Transaction;
import com.stock_trading_simulation_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private TransactionRepository transactionRepository;

    // Is method ko TransactionRepository mein add karna hoga
    public List<Transaction> getTransactionsByUserId(Long userId) {
        // Assume you have added this method to TransactionRepository
        // Agar nahi add kiya hai, to Repository mein yeh line add karein: 
        // List<Transaction> findByUser_Id(Long userId);
        return transactionRepository.findByUser_Id(userId); 
    }

    /**
     * User ka Total Profit/Loss (P&L) calculate karta hai.
     */
    public Map<String, BigDecimal> calculateProfitLoss(Long userId) {
        List<Transaction> transactions = getTransactionsByUserId(userId);
        BigDecimal totalProfitLoss = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            BigDecimal transactionAmount = t.getPrice().multiply(BigDecimal.valueOf(t.getQuantity()));
            
            if ("SELL".equalsIgnoreCase(t.getTransactionType())) {
                // Selling se profit ya loss ho sakta hai
                totalProfitLoss = totalProfitLoss.add(transactionAmount);
            } else if ("BUY".equalsIgnoreCase(t.getTransactionType())) {
                // Buying se paisa kharch hota hai (loss ya negative flow)
                totalProfitLoss = totalProfitLoss.subtract(transactionAmount);
            }
        }

        // Output Map banaayein
        Map<String, BigDecimal> results = new HashMap<>();
        results.put("total_pnl", totalProfitLoss);
        
        return results;
    }
}