package com.ecommerce.stock.domain;

import java.util.Map;

import com.ecommerce.stock.domain.entity.StockItem;
import com.ecommerce.stock.domain.entity.StockReservation;
import com.ecommerce.stock.domain.entity.StockReservationItem;
import com.ecommerce.stock.domain.event.StockConfirmedEvent;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.event.StockReleasedEvent;
import com.ecommerce.stock.domain.event.StockReservedEvent;
import com.ecommerce.stock.domain.event.StockUnavailableEvent;
import com.ecommerce.stock.domain.valueobject.ProductId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @Override
    public StockEvent finalizeStockReservation(StockReservation stockReservation, Map<ProductId, StockItem> stockItems) {
        for (StockReservationItem item : stockReservation.getItems()) {
            StockItem stockItem = stockItems.get(item.getProductId());
            if (stockItem == null) {
                // in a real scenario, we should probably send a dedicated event to notify the order service to cancel the order and cancel the paiement
                // and then cancel the stock reservation. Here we limit the complexity of this demo, log an error and ignore this item.
                log.error("a stock item has been previoulsy reserved but does not exist any more");
            } else {
                stockItem.confirmReservation(item.getQuantity());
            }
        }
        stockReservation.finalizeReservation();
        return new StockConfirmedEvent(stockReservation);
    }

    @Override
    public StockEvent releaseStockReservation(StockReservation stockReservation, Map<ProductId, StockItem> stockItems) {
        for (StockReservationItem item : stockReservation.getItems()) {
            StockItem stockItem = stockItems.get(item.getProductId());
            if (stockItem == null) {
                // in a real scenario, we should probably send a dedicated event to notify the order service to cancel the order and cancel the paiement
                // and then cancel the stock reservation. Here we limit the complexity of this demo, log an error and ignore this item.
                log.error("a stock item has been previoulsy reserved but does not exist any more");
            } else {
                stockItem.releaseReservation(item.getQuantity());
            }
        }
        stockReservation.release();
        return new StockReleasedEvent(stockReservation);
    }

}
