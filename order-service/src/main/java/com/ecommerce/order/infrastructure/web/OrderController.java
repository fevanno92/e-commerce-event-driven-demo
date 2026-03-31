package com.ecommerce.order.infrastructure.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.application.ports.input.OrderApplicationService;

@RestController
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @Autowired
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

}
