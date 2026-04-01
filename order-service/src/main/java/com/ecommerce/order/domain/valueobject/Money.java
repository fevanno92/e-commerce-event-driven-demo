package com.ecommerce.order.domain.valueobject;

import java.math.BigDecimal;

public class Money {
    
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }
    
    public boolean isValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
}
