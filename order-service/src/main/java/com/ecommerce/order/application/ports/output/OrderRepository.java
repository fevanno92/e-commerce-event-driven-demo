package com.ecommerce.order.application.ports.output;

import java.util.List;

import com.ecommerce.order.domain.entity.Order;

public interface OrderRepository {
    public void save(Order order);

    public List<Order> getAllOrders();
}
