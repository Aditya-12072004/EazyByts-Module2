package com.stock_trading_simulation_system.service;

import com.stock_trading_simulation_system.model.Portfolio;
import com.stock_trading_simulation_system.model.Transaction;
import com.stock_trading_simulation_system.model.User;
import com.stock_trading_simulation_system.repository.PortfolioRepository;
import com.stock_trading_simulation_system.repository.TransactionRepository;
import com.stock_trading_simulation_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TradingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MarketDataService marketDataService; // Naya: MarketDataService ko inject kiya gaya

    @Transactional
    public void buyStock(Long userId, String stockSymbol, int quantity) { // 'stockPrice' parameter ko hataya gaya
        
        // Naya: Live stock price fetch karein MarketDataService se
        BigDecimal stockPrice = marketDataService.getLivePrice(stockSymbol); 

        // Baaki ka logic same rahega
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal transactionCost = stockPrice.multiply(BigDecimal.valueOf(quantity));

        if (user.getBalance().compareTo(transactionCost) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(transactionCost));
        userRepository.save(user);

        Optional<Portfolio> optionalPortfolio = portfolioRepository.findByUser_IdAndStockSymbol(userId, stockSymbol);
        if (optionalPortfolio.isPresent()) {
            Portfolio portfolio = optionalPortfolio.get();
            int newQuantity = portfolio.getQuantity() + quantity;
            BigDecimal newAveragePrice = (portfolio.getAveragePrice().multiply(BigDecimal.valueOf(portfolio.getQuantity()))
                                            .add(transactionCost))
                                            .divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);
            portfolio.setQuantity(newQuantity);
            portfolio.setAveragePrice(newAveragePrice);
            portfolioRepository.save(portfolio);
        } else {
            Portfolio newPortfolioItem = new Portfolio();
            newPortfolioItem.setUser(user);
            newPortfolioItem.setStockSymbol(stockSymbol);
            newPortfolioItem.setQuantity(quantity);
            newPortfolioItem.setAveragePrice(stockPrice);
            portfolioRepository.save(newPortfolioItem);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setStockSymbol(stockSymbol);
        transaction.setTransactionType("BUY");
        transaction.setQuantity(quantity);
        transaction.setPrice(stockPrice);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
    
    @Transactional
    public void sellStock(Long userId, String stockSymbol, int quantity) { // 'stockPrice' parameter ko hataya gaya
        
        // Naya: Live stock price fetch karein
        BigDecimal stockPrice = marketDataService.getLivePrice(stockSymbol);
        
        // Baaki ka logic same rahega
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Portfolio portfolio = portfolioRepository.findByUser_IdAndStockSymbol(userId, stockSymbol)
                .orElseThrow(() -> new RuntimeException("Stock not found in user's portfolio"));

        if (portfolio.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock quantity in portfolio");
        }

        BigDecimal transactionValue = stockPrice.multiply(BigDecimal.valueOf(quantity));

        user.setBalance(user.getBalance().add(transactionValue));
        userRepository.save(user);

        portfolio.setQuantity(portfolio.getQuantity() - quantity);
        if (portfolio.getQuantity() == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolioRepository.save(portfolio);
        }
        
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setStockSymbol(stockSymbol);
        transaction.setTransactionType("SELL");
        transaction.setQuantity(quantity);
        transaction.setPrice(stockPrice);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}