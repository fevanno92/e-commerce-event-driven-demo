package com.ecommerce.stock.application.dto;

import java.util.UUID;

public record StockItemDTO(
    UUID stockItemId,
    UUID productId,
    int totalQuantity,
    int reservedQuantity
) {
    
}
