package com.ecommerce.stock.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.BaseEntity;
import com.ecommerce.stock.domain.exception.InvalidStockReservationException;
import com.ecommerce.stock.domain.valueobject.ProductId;
import com.ecommerce.stock.domain.valueobject.StockReservationItemId;

public class StockReservationItem extends BaseEntity<StockReservationItemId> {
    private final ProductId productId;
    private final int quantity;

    public StockReservationItem(ProductId productId, int quantity) {
        super(new StockReservationItemId(UUID.randomUUID()));
        validateBasicInvariants(productId, quantity);
        this.productId = productId;
        this.quantity = quantity;
    }

    public StockReservationItem(StockReservationItemId id, ProductId productId, int quantity) {
        super(id);
        validateBasicInvariants(productId, quantity);
        this.productId = productId;
        this.quantity = quantity;
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    private void validateBasicInvariants(ProductId productId, int quantity) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidStockReservationException("Stock Reservation Item ID is required");
        }
        if (productId == null || !productId.isDefined()) {
            throw new InvalidStockReservationException("Product ID is required");
        }
        if (quantity <= 0) {
            throw new InvalidStockReservationException("Quantity must be positive");
        }
    }
}
