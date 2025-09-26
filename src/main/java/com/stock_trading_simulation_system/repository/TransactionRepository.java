package com.stock_trading_simulation_system.repository;

import com.stock_trading_simulation_system.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Naya: List ko import kiya gaya

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Custom method to fetch all transactions for a specific user.
    // Spring Data JPA automatically provides the implementation for this method.
    List<Transaction> findByUser_Id(Long userId); // Naya: Yeh method add kiya gaya hai
}