package com.ecommerce.order.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ecommerce.order.domain.entity.OrderStatus;


public record OrderDTO (
    UUID orderId,
    UUID customerId,
    Instant createdAt,
    OrderStatus status,
    List<OrderItemDTO> items
) {
    
}
