package com.ecommerce.payment.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class CustomerId extends BaseUUIDValueObject {

    public CustomerId(UUID id) {
        super(id);
    }

}