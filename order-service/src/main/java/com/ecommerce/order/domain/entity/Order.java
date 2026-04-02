package com.ecommerce.order.domain.entity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.order.domain.exception.InvalidOrderException;
import com.ecommerce.order.domain.valueobject.CustomerId;
import com.ecommerce.order.domain.valueobject.OrderId;
import com.ecommerce.order.domain.valueobject.ProductId;

public class Order extends RootAggregate<OrderId> {

    private final CustomerId customerId;
    private final List<OrderItem> items;
    private Instant createdAt;
    private OrderStatus status;

    public Order(CustomerId customerId, List<OrderItem> items) {
        super();
        this.customerId = customerId;
        this.items = items;
    }

    public Order(OrderId id, CustomerId customerId, List<OrderItem> items, Instant createdAt, OrderStatus status) {
        super(id);
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
        this.status = status;
    }

    public void validate(Map<ProductId, Product> products) {
        if (customerId == null || !customerId.isDefined()) {
            throw new InvalidOrderException("Customer ID is not defined");
        }
        
        for (OrderItem item : items) {
            item.validate(products.get(item.getProductId()));
        }
    }

    public void initialize() {
        setId(new OrderId(UUID.randomUUID()));
        this.createdAt = Instant.now();
        this.status = OrderStatus.PENDING;

        for (OrderItem item : items) {
            item.initialize(getId());
        }
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
