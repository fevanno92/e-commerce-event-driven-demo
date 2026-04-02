package com.ecommerce.order.infrastructure.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.application.dto.CreateOrderCommand;
import com.ecommerce.order.application.dto.GetOrderResponse;
import com.ecommerce.order.application.ports.input.OrderApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @Autowired
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public void createOrder(@RequestBody @Valid CreateOrderCommand createOrderCommand) {
        orderApplicationService.createOrder(createOrderCommand);
    }

    @GetMapping
    public List<GetOrderResponse> getAllOrders() {
        return orderApplicationService.getAllOrders();
    }

}
