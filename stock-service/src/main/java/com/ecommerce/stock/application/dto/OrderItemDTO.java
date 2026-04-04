package com.ecommerce.stock.application.dto;

import java.util.UUID;

public record OrderItemDTO(
    UUID productId,
    int quantity
) {
}
