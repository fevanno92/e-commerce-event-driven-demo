package com.ecommerce.order.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class OrderItemId extends BaseUUIDValueObject {
    public OrderItemId(UUID id) {
        super(id);        
    }   
}
