package com.ecommerce.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.order.domain.OrderDomainService;
import com.ecommerce.order.domain.OrderDomainServiceImpl;

@Configuration
public class OrderApplicationConfig {
    
    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

}
