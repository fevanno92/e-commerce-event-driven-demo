package com.ecommerce.stock.domain;

import java.util.Map;

import com.ecommerce.stock.domain.entity.StockItem;
import com.ecommerce.stock.domain.entity.StockReservation;
import com.ecommerce.stock.domain.entity.StockReservationItem;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.event.StockReservedEvent;
import com.ecommerce.stock.domain.event.StockUnavailableEvent;
import com.ecommerce.stock.domain.valueobject.ProductId;

public class StockDomainServiceImpl implements StockDomainService {

    @Override
    public StockEvent validateAndReserveStock(StockReservation stockReservation, Map<ProductId, StockItem> stockItems) {

        // validate stock item availability
        for (StockReservationItem item : stockReservation.getItems()) {
            StockItem stockItem = stockItems.get(item.getProductId());
            if (stockItem == null) {
                stockReservation.cancel();
                return new StockUnavailableEvent(stockReservation,
                        "Stock item not found for product: " + item.getProductId());
            }
            if (!stockItem.canReserve(item.getQuantity())) {
                stockReservation.cancel();
                return new StockUnavailableEvent(stockReservation,
                        "Insufficient stock for product: " + item.getProductId());
            }
        }

        // reserve stock
        for (StockReservationItem item : stockReservation.getItems()) {
            StockItem stockItem = stockItems.get(item.getProductId());
            stockItem.reserveQuantity(item.getQuantity());
        }
        stockReservation.confirm();
        return new StockReservedEvent(stockReservation);
    }

}
