package com.ecommerce.order.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.ecommerce.common.domain.entity.BaseEntity;
import com.ecommerce.order.domain.exception.InvalidOrderException;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.OrderId;
import com.ecommerce.order.domain.valueobject.OrderItemId;
import com.ecommerce.order.domain.valueobject.ProductId;

public class OrderItem extends BaseEntity<OrderItemId> {

    private static final BigDecimal PRICE_DELTA = new BigDecimal("0.1");
    
    private OrderId orderId;
    private final ProductId productId;
    private final int quantity;
    private final Money price;

    public OrderItem(ProductId productId, int quantity, Money price) {
        super(new OrderItemId(UUID.randomUUID()));
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

    public OrderId getOrderId() {
        return orderId;
    }

    public void validate(Product product) {
        if (product == null) {
            throw new InvalidOrderException("Product not found for ID: " + productId);
        }

        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero for product ID: " + productId);
        }

        if (price == null || !price.isValid()) {
            throw new InvalidOrderException("Invalid price for product ID: " + productId);
        }

        BigDecimal priceDifference = price.getAmount().subtract(product.getPrice().getAmount()).abs();
        if (priceDifference.compareTo(PRICE_DELTA) > 0) {
            throw new InvalidOrderException("Price mismatch for product ID: " + productId);
        }
    }

    public void initialize(OrderId orderId) {
        setId(new OrderItemId(UUID.randomUUID()));
        this.orderId = orderId;
    }
}
