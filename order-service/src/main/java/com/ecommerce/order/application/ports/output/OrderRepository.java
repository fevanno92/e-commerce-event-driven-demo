package com.ecommerce.order.application.ports.output;

import com.ecommerce.order.domain.entity.Order;

public interface OrderRepository {
    public void save(Order order);
}
