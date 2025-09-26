package com.stock_trading_simulation_system.service;

import com.stock_trading_simulation_system.model.User;
import com.stock_trading_simulation_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        user.setBalance(new BigDecimal("10000.00")); // Har naye user ko ₹10,000 ka virtual balance do
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }
    
    // findById method jo database se user dhoondega
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    // Naya: findByUsername method add kiya gaya hai (Frontend ki zaroorat ke liye)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}