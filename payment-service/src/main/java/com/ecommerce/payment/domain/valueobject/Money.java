package com.ecommerce.payment.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {
    
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }
    
    public boolean isValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public Money subtract(Money money) {
        return new Money(setScale(this.amount.subtract(money.getAmount())));
    }

    private BigDecimal setScale(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isLessThan(Money money) {
        return amount.compareTo(money.getAmount()) < 0;
    }
}