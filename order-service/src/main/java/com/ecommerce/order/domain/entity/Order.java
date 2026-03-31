package com.ecommerce.order.domain.entity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.order.domain.valueobject.CustomerId;
import com.ecommerce.order.domain.valueobject.OrderId;

public class Order extends RootAggregate<OrderId> {

    private final CustomerId customerId;
    private final Instant createdAt;
    private final List<OrderItem> items;
    private OrderStatus status;

    public Order(OrderId id, CustomerId customerId, List<OrderItem> items) {
        super(new OrderId(UUID.randomUUID()));
        this.customerId = customerId;
        this.createdAt = Instant.now();
        this.items = items;
        this.status = OrderStatus.PENDING;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}
