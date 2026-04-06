package com.ecommerce.stock.domain;

import java.util.Map;

import com.ecommerce.stock.domain.entity.StockItem;
import com.ecommerce.stock.domain.entity.StockReservation;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.valueobject.ProductId;

public interface StockDomainService {
    StockEvent validateAndReserveStock(StockReservation stockReservation, Map<ProductId, StockItem> stockItems);
    StockEvent finalizeStockReservation(StockReservation stockReservation, Map<ProductId, StockItem> stockItems);
    StockEvent releaseStockReservation(StockReservation stockReservation, Map<ProductId, StockItem> stockItems);
}
