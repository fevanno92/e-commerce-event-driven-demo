package com.ecommerce.stock.application.dto;

import java.util.UUID;

public record StockItemDTO(
    UUID productId,
    int quantity
) {
}
