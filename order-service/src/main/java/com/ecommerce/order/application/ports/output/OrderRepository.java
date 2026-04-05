package com.ecommerce.order.application.ports.output;

import java.util.List;
import java.util.Optional;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.valueobject.OrderId;

public interface OrderRepository {
    public void save(Order order);

    public List<Order> getAllOrders();

    public Optional<Order> findById(OrderId id);
}
