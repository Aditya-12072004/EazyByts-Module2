package com.stock_trading_simulation_system.repository;

import com.stock_trading_simulation_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically finds a user by their username
    Optional<User> findByUsername(String username);
}