package com.ecommerce.order.domain;

import java.util.Map;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.Product;
import com.ecommerce.order.domain.event.OrderCreatedEvent;
import com.ecommerce.order.domain.valueobject.ProductId;

public interface OrderDomainService {
    public OrderCreatedEvent validateOrderProducts(Order order, Map<ProductId, Product> products);
}
