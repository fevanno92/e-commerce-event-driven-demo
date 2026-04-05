package com.ecommerce.payment.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class PaymentId extends BaseUUIDValueObject {

    public PaymentId(UUID id) {
        super(id);
    }

}