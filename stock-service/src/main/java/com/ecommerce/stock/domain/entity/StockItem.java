package com.ecommerce.stock.domain.entity;

import com.ecommerce.common.domain.entity.BaseEntity;
import com.ecommerce.stock.domain.exception.InvalidStockItemException;
import com.ecommerce.stock.domain.valueobject.ProductId;
import com.ecommerce.stock.domain.valueobject.StockItemId;

public class StockItem extends BaseEntity<StockItemId> {
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
    public StockItem(StockItemId id, ProductId productId) {
        super(id);
        validateBasicInvariants(id, productId);
        this.productId = productId;
        this.totalQuantity = 0;
        this.reservedQuantity = 0;
    }

    // Constructor for loading an existing StockItem from persistence
    public StockItem(StockItemId id, ProductId productId, int totalQuantity, int reservedQuantity) {
        super(id);
        validateBasicInvariants(id, productId);
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

    private void validateBasicInvariants(StockItemId id, ProductId productId) {
        if (id == null) {
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
