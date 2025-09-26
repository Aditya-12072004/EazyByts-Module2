package com.stock_trading_simulation_system.service;

import com.stock_trading_simulation_system.model.Portfolio;
import com.stock_trading_simulation_system.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    public List<Portfolio> findByUserId(Long userId) {
        return portfolioRepository.findByUser_Id(userId);
    }
}