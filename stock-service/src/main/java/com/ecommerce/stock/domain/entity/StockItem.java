package com.ecommerce.stock.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.stock.domain.exception.InvalidStockItemException;
import com.ecommerce.stock.domain.valueobject.ProductId;
import com.ecommerce.stock.domain.valueobject.StockItemId;

public class StockItem extends RootAggregate<StockItemId> {
    private final ProductId productId;
    private int totalQuantity;
    private int reservedQuantity;

    public ProductId getProductId() {
        return productId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    // Constructor for creating a new StockItem (initial state)
    public StockItem(ProductId productId) {
        super(new StockItemId(UUID.randomUUID()));
        validateBasicInvariants(productId);
        this.productId = productId;
        this.totalQuantity = 0;
        this.reservedQuantity = 0;
    }

    // Constructor for loading an existing StockItem from persistence
    public StockItem(StockItemId id, ProductId productId, int totalQuantity, int reservedQuantity) {
        super(id);
        validateBasicInvariants(productId);
        validateStateInvariants(totalQuantity, reservedQuantity);
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public void addQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockItemException("Quantity to add must be positive");
        }
        this.totalQuantity += quantity;
    }

    public void reserveQuantity(int quantity) {
        if (!canReserve(quantity)) {
            throw new InvalidStockItemException("Insufficient stock available for reservation");
        }
        this.reservedQuantity += quantity;
    }

    public boolean canReserve(int quantity) {
        if (quantity <= 0) {
            return false;
        }
        return totalQuantity - reservedQuantity >= quantity;
    }

    public void confirmReservation(int quantity) {
        this.totalQuantity -= quantity;
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }

    public void releaseReservation(int quantity) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }

    private void validateBasicInvariants(ProductId productId) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidStockItemException("Stock Item ID is required");
        }
        if (productId == null || !productId.isDefined()) {
            throw new InvalidStockItemException("Product ID is required");
        }
    }

    private void validateStateInvariants(int total, int reserved) {
        if (total < 0) {
            throw new InvalidStockItemException("Total quantity cannot be negative");
        }
        if (reserved < 0) {
            throw new InvalidStockItemException("Reserved quantity cannot be negative");
        }
        if (reserved > total) {
            throw new InvalidStockItemException("Reserved quantity cannot exceed total quantity");
        }
    }

}
