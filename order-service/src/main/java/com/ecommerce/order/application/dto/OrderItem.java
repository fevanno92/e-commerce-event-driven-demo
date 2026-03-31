package com.ecommerce.order.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItem(
    @NotNull UUID productId,
    @Positive int quantity,
    @Positive @NotNull BigDecimal price
) {
    
}
