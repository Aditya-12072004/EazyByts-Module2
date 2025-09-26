package com.stock_trading_simulation_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String stockSymbol;

    @Column(nullable = false)
    private String transactionType; 

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price; 

    @Column(nullable = false)
    private LocalDateTime timestamp;

}