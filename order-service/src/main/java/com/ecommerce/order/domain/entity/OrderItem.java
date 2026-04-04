package com.ecommerce.order.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.BaseEntity;
import com.ecommerce.order.domain.exception.InvalidOrderException;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.OrderItemId;
import com.ecommerce.order.domain.valueobject.ProductId;

public class OrderItem extends BaseEntity<OrderItemId> {

    private final ProductId productId;
    private final int quantity;
    private final Money price;

    public OrderItem(ProductId productId, int quantity, Money price) {
        super(new OrderItemId(UUID.randomUUID()));
        validateBasicInvariants(productId);
        validateState(quantity, price);
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem(OrderItemId id, ProductId productId, int quantity, Money price) {
        super(id);
        validateBasicInvariants(productId);
        validateState(quantity, price);
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    private void validateBasicInvariants(ProductId productId) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidOrderException("Order Item ID is not defined");
        }
        if (productId == null || !productId.isDefined()) {
            throw new InvalidOrderException("Product ID is not defined");
        }               
    }

    private void validateState(int quantity, Money price) {
        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero");
        }
        if (price == null || !price.isValid()) {
            throw new InvalidOrderException("Price must be valid");
        }
    }
}
