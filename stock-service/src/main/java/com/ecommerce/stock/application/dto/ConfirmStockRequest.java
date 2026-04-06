package com.ecommerce.stock.application.dto;

import java.util.UUID;

public record ConfirmStockRequest(
    UUID orderId
) {}
