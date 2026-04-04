package com.ecommerce.stock.application.ports.input;

import com.ecommerce.stock.application.dto.ReserveStockRequest;

public interface StockMessageListener {
    void reserveStock(ReserveStockRequest request);
}
