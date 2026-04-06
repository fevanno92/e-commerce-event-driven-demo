package com.ecommerce.stock.domain.entity;

import java.util.List;
import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.stock.domain.exception.InvalidStockReservationException;
import com.ecommerce.stock.domain.valueobject.OrderId;
import com.ecommerce.stock.domain.valueobject.StockReservationId;

public class StockReservation extends RootAggregate<StockReservationId> {
    private final OrderId orderId;   
    private final List<StockReservationItem> items;
    private StockReservationStatus status;

    public StockReservation(OrderId orderId, List<StockReservationItem> items) {
        super(new StockReservationId(UUID.randomUUID()));
        validateBasicInvariants(orderId, items);
        this.orderId = orderId;
        this.items = items;
        this.status = StockReservationStatus.PENDING;
    }

    public StockReservation(StockReservationId id, OrderId orderId, List<StockReservationItem> items, StockReservationStatus status) {
        super(id);
        validateBasicInvariants(orderId, items);
        validateStatus(status);
        this.orderId = orderId;
        this.items = items;
        this.status = status;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public List<StockReservationItem> getItems() {
        return items;
    }

    public StockReservationStatus getStatus() {
        return status;
    }

    public void cancel() {
        if (this.status != StockReservationStatus.PENDING) {
            throw new InvalidStockReservationException("Stock Reservation must be in PENDING status to be cancelled, current status: " + status);
        }
        this.status = StockReservationStatus.CANCELLED;
    }

    public void confirm() {
        if (this.status != StockReservationStatus.PENDING) {
            throw new InvalidStockReservationException("Stock Reservation must be in PENDING status to be confirmed, current status: " + status);
        }
        this.status = StockReservationStatus.CONFIRMED;
    }

    public void finalizeReservation() {
        if (this.status != StockReservationStatus.CONFIRMED) {
            throw new InvalidStockReservationException("Stock Reservation must be in CONFIRMED status to be finalized, current status: " + status);
        }
        this.status = StockReservationStatus.FINALIZED;
    }

    public void release() {
        if (this.status != StockReservationStatus.CONFIRMED) {
            throw new InvalidStockReservationException("Stock Reservation must be in CONFIRMED status to be released, current status: " + status);
        }
        this.status = StockReservationStatus.RELEASED;
    }

    private void validateBasicInvariants(OrderId orderId, List<StockReservationItem> items) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidStockReservationException("Stock Reservation ID is required");
        }
        if (orderId == null || !orderId.isDefined()) {
            throw new InvalidStockReservationException("Order ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidStockReservationException("Items are required");
        }
    }

    private void validateStatus(StockReservationStatus status) {
        if (status == null) {
            throw new InvalidStockReservationException("Status is required");
        }
    }
}
