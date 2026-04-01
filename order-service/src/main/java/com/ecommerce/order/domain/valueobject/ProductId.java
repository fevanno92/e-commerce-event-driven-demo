package com.ecommerce.order.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class ProductId extends BaseUUIDValueObject {

    public ProductId(UUID id) {
        super(id);        
    }
    
}
