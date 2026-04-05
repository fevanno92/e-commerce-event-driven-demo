package com.ecommerce.payment.domain.entity;

import java.util.UUID;

import com.ecommerce.common.domain.entity.RootAggregate;
import com.ecommerce.payment.domain.exception.InvalidPaymentException;
import com.ecommerce.payment.domain.valueobject.CustomerId;
import com.ecommerce.payment.domain.valueobject.Money;
import com.ecommerce.payment.domain.valueobject.OrderId;
import com.ecommerce.payment.domain.valueobject.PaymentId;

public class Payment extends RootAggregate<PaymentId> {
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money amount;
    private PaymentStatus status;

    public Payment(OrderId orderId, CustomerId customerId, Money amount, PaymentStatus status) {
        super(new PaymentId(UUID.randomUUID()));
        validateState(orderId, customerId, amount, status);
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public Payment(PaymentId paymentId, OrderId orderId, CustomerId customerId, Money amount, PaymentStatus status) {
        super(paymentId);
        validateState(orderId, customerId, amount, status);
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void markAsSuccess() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    private void validateState(OrderId orderId, CustomerId customerId, Money amount, PaymentStatus status) {
        if (getId() == null || !getId().isDefined()) {
            throw new InvalidPaymentException("Payment ID is required");
        }
        if (orderId == null || !orderId.isDefined()) {
            throw new InvalidPaymentException("Order ID is required");
        }
        if (customerId == null || !customerId.isDefined()) {
            throw new InvalidPaymentException("Customer ID is required");
        }
        if (amount == null || !amount.isValid()) {
            throw new InvalidPaymentException("Amount is required");
        }
        if (status == null) {
            throw new InvalidPaymentException("Status is required");
        }
    }

}
