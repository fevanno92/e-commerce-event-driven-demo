package com.ecommerce.order.infrastructure.repository;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.domain.entity.Order;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public void save(Order order) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
    
}
