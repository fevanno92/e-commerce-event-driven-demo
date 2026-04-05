package com.ecommerce.payment.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.payment.domain.exception.InvalidPaymentException;
import com.ecommerce.payment.domain.valueobject.OrderId;
import com.ecommerce.payment.domain.valueobject.PaymentId;

public class Payment extends RootAggregate<PaymentId> {
    private final OrderId orderId;
    
    public OrderId getOrderId() {
        return orderId;
    }
    
    public Payment(OrderId orderId) {
        super(new PaymentId(UUID.randomUUID()));
        validateBasicInvariants(orderId);
        this.orderId = orderId;
    }
    
    private void validateBasicInvariants(OrderId orderId) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidPaymentException("Payment ID is required");
        }
        if (orderId == null || !orderId.isDefined()) {
            throw new InvalidPaymentException("Order ID is required");
        }
    }

}
