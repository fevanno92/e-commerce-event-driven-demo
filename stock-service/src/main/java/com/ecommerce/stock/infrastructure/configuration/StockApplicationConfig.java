package com.ecommerce.stock.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.stock.domain.StockDomainService;
import com.ecommerce.stock.domain.StockDomainServiceImpl;

@Configuration
public class StockApplicationConfig {
    
    @Bean
    public StockDomainService stockDomainService() {
        return new StockDomainServiceImpl();
    }

}
