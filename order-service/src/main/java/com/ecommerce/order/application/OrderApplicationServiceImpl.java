package com.ecommerce.order.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.order.application.ports.input.OrderApplicationService;
import com.ecommerce.order.domain.OrderDomainService;

@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {
    
    private final OrderDomainService orderDomainService;

    @Autowired
    public OrderApplicationServiceImpl(OrderDomainService orderDomainService) {
        this.orderDomainService = orderDomainService;
    }

}
