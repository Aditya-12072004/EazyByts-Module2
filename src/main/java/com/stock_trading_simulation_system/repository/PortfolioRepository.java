package com.stock_trading_simulation_system.repository;

import com.stock_trading_simulation_system.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // Finds a user's entire portfolio
    List<Portfolio> findByUser_Id(Long userId);

    // Finds a specific stock in a user's portfolio
    Optional<Portfolio> findByUser_IdAndStockSymbol(Long userId, String stockSymbol);
}