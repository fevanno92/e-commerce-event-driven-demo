package com.ecommerce.stock.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddStockRequest(
    @NotNull UUID productId,
    @Positive int quantity
) {
}
