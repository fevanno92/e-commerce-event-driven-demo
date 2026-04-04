package com.ecommerce.stock.application.dto;

import java.util.List;
import java.util.UUID;

public record ReserveStockRequest(
    UUID orderId,
    List<StockItemDTO> items
) {
}
