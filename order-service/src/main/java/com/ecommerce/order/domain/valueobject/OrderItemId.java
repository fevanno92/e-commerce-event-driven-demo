package com.ecommerce.order.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseValueObject;

public class OrderItemId extends BaseValueObject<UUID> {
    public OrderItemId(UUID value) {
        super(value);        
    }   
}
