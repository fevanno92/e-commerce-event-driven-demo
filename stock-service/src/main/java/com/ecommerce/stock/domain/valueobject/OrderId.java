package com.ecommerce.stock.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class OrderId extends BaseUUIDValueObject {

    public OrderId(UUID id) {
        super(id);
    }

}