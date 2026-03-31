package com.ecommerce.order.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseValueObject;

public class CustomerId extends BaseValueObject<UUID> {

    public CustomerId(UUID value) {
        super(value);        
    }
    
}
