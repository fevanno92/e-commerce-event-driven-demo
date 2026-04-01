package com.ecommerce.order.application.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderCommand(
    @NotNull UUID customerId,
    @NotEmpty @Valid List<OrderItem> items
) {
    
}
