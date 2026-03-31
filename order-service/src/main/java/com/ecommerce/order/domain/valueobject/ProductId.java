package com.ecommerce.order.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseValueObject;

public class ProductId extends BaseValueObject<UUID> {

    public ProductId(UUID value) {
        super(value);        
    }
    
}
