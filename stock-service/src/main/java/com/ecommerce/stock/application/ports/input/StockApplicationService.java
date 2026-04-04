package com.ecommerce.stock.application.ports.input;

import java.util.List;

import com.ecommerce.stock.application.dto.AddStockRequest;
import com.ecommerce.stock.application.dto.StockItemDTO;

public interface StockApplicationService {
    StockItemDTO addStock(AddStockRequest request);
    List<StockItemDTO> getAllStockItems();
}
