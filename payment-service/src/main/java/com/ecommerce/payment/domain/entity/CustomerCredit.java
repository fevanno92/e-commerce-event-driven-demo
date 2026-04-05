package com.ecommerce.payment.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.BaseEntity;
import com.ecommerce.payment.domain.exception.InvalidCreditException;
import com.ecommerce.payment.domain.valueobject.CustomerCreditId;
import com.ecommerce.payment.domain.valueobject.CustomerId;
import com.ecommerce.payment.domain.valueobject.Money;

public class CustomerCredit extends BaseEntity<CustomerCreditId> {

    private final CustomerId customerId;
    private Money creditAmount;

    public CustomerCredit(CustomerId customerId, Money creditAmount) {
        super(new CustomerCreditId(UUID.randomUUID()));
        validateState(customerId, creditAmount);
        this.customerId = customerId;
        this.creditAmount = creditAmount;
    }

    public CustomerCredit(CustomerCreditId customerCreditId, CustomerId customerId, Money creditAmount) {
        super(customerCreditId);
        validateState(customerId, creditAmount);
        this.customerId = customerId;
        this.creditAmount = creditAmount;
    }

    public Money getCreditAmount() {
        return creditAmount;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

   public void subtractCreditAmount(Money amount) {
        creditAmount = creditAmount.subtract(amount);
    }

    public void validateState(CustomerId customerId, Money creditAmount) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidCreditException("Customer Credit ID is required");
        }
        if (customerId == null || !customerId.isDefined()) {
            throw new InvalidCreditException("Customer ID is required");
        }
        if (creditAmount == null || !creditAmount.isValid()) {
            throw new InvalidCreditException("Credit amount is required");
        }
    }

}
