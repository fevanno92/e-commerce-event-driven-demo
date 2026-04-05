package com.ecommerce.payment.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class CustomerCreditId extends BaseUUIDValueObject {

    public CustomerCreditId(UUID id) {
        super(id);
    }

}