package com.ecommerce.order.domain.entity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.order.domain.exception.InvalidOrderException;
import com.ecommerce.order.domain.valueobject.CustomerId;
import com.ecommerce.order.domain.valueobject.OrderId;

public class Order extends RootAggregate<OrderId> {

    private final CustomerId customerId;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private OrderStatus status;

    public Order(CustomerId customerId, List<OrderItem> items) {
        super(new OrderId(UUID.randomUUID()));
        validateBasicInvariants(customerId);
        this.customerId = customerId;
        this.items = items;
        this.createdAt = Instant.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(OrderId id, CustomerId customerId, List<OrderItem> items, Instant createdAt, OrderStatus status) {
        super(id);
        validateBasicInvariants(customerId);
        validateStatusAndCreatedAt(status, createdAt);
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
        this.status = status;
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

    private void validateBasicInvariants(CustomerId customerId) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidOrderException("Order ID is not defined");
        }
        if (customerId == null || !customerId.isDefined()) {
            throw new InvalidOrderException("Customer ID is not defined");
        }
    }

    private void validateStatusAndCreatedAt(OrderStatus status, Instant createdAt) {
        if (status == null) {
            throw new InvalidOrderException("Status is not defined");
        }
        if (createdAt == null) {
            throw new InvalidOrderException("Created at is not defined");
        }
    }

}
