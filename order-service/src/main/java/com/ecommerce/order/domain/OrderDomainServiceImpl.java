package com.ecommerce.order.domain;

import java.util.Map;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.Product;
import com.ecommerce.order.domain.event.OrderCreatedEvent;
import com.ecommerce.order.domain.valueobject.ProductId;

public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitializeOrder(Order order, Map<ProductId, Product> products) {
        
        order.validate(products);
        order.initialize();

        return new OrderCreatedEvent(order);
    }

}
