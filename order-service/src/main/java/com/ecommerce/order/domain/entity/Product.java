package com.ecommerce.order.domain.entity;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.ProductId;

public class Product extends RootAggregate<ProductId> {
    private String name;
    private String description;
    private Money price;

    public Product(ProductId id, String name, String description, Money price) {
        super(id);
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
    
    public boolean isValid() {
        return getId() != null && getId().isValid() && name != null && !name.isBlank() && price != null && price.isValid();
    }

}
