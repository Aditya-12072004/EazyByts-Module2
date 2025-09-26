// StockTradingSimulationSystemApplication.java file mein
package com.stock_trading_simulation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class StockTradingSimulationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTradingSimulationSystemApplication.class, args);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}