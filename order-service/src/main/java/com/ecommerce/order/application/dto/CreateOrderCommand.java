package com.ecommerce.order.application.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderCommand(
    @NotBlank String customerId,
    @NotEmpty @Valid List<OrderItem> items
) {
    
}
