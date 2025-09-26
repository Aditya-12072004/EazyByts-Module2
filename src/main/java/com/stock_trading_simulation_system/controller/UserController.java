package com.stock_trading_simulation_system.controller;

import com.stock_trading_simulation_system.model.User;
import com.stock_trading_simulation_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional; // Naya: Optional ko import kiya gaya

@RestController // This marks the class as a REST controller
@RequestMapping("/api/users") // Base URL for all methods in this controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register") // This method will handle POST requests to /api/users/register
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login") // Handles login requests to /api/users/login
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (userService.loginUser(username, password).isPresent()) {
            return new ResponseEntity<>("Login Successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{userId}") // Naya endpoint: User details fetch karne ke liye
    public ResponseEntity<User> getUserDetails(@PathVariable Long userId) {
        Optional<User> user = userService.findById(userId); // Naya: Service se user ko dhoondha gaya
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/username/{username}") // Naya: Login ke baad user data fetch karne ke liye
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username); // Naya service method ka assumption
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}