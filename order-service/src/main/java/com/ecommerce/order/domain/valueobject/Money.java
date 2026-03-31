package com.ecommerce.order.domain.valueobject;

import java.math.BigDecimal;

import com.ecommerce.common.domain.valueobject.BaseValueObject;

public class Money extends BaseValueObject<BigDecimal> {
    
    public Money(BigDecimal value) {
        super(value);
    }
    
    public boolean isValid() {
        return getValue() != null && getValue().compareTo(BigDecimal.ZERO) > 0;
    }
    
}
