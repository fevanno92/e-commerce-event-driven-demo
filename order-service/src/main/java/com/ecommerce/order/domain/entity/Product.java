package com.ecommerce.order.domain.entity;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.order.domain.exception.InvalidProductException;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.ProductId;

public class Product extends RootAggregate<ProductId> {
    private String name;
    private String description;
    private Money price;

    public Product(ProductId id, String name, String description, Money price) {
        super(id);
        validateInvariants(id, name, description, price);
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }
    
    private void validateInvariants(ProductId id, String name, String description, Money price) {
        if (id == null || !id.isDefined()) {
            throw new InvalidProductException("Product ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("Product name is required");
        }       
        if (price == null || !price.isValid()) {
            throw new InvalidProductException("Product price is required and must be positive");
        }
    }

}
