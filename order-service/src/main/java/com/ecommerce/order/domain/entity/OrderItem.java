package com.ecommerce.order.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.BaseEntity;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.OrderItemId;
import com.ecommerce.order.domain.valueobject.ProductId;

public class OrderItem extends BaseEntity<OrderItemId> {
    
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
}

