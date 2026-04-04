package com.ecommerce.stock.application.outbox.payload;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItemPayload {
    private UUID productId;
    private int quantity;
}
