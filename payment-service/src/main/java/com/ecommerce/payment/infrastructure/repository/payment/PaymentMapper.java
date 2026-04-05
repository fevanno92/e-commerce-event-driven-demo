package com.ecommerce.payment.infrastructure.repository.payment;

import org.springframework.stereotype.Component;

import com.ecommerce.common.domain.exception.CorruptedDataPersistenceException;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.exception.InvalidPaymentException;
import com.ecommerce.payment.domain.valueobject.CustomerId;
import com.ecommerce.payment.domain.valueobject.Money;
import com.ecommerce.payment.domain.valueobject.OrderId;
import com.ecommerce.payment.domain.valueobject.PaymentId;

@Component
public class PaymentMapper {

    public PaymentEntity mapToEntity(Payment payment) {
        return new PaymentEntity(
                payment.getId().getValue(),
                payment.getOrderId().getValue(),
                payment.getCustomerId().getValue(),
                payment.getAmount().getAmount(),
                payment.getStatus());
    }

    public Payment mapToDomain(PaymentEntity paymentEntity) {
        try {
            return new Payment(
                    new PaymentId(paymentEntity.getId()),
                    new OrderId(paymentEntity.getOrderId()),
                    new CustomerId(paymentEntity.getCustomerId()),
                    new Money(paymentEntity.getAmount()),
                    paymentEntity.getStatus());
        } catch (InvalidPaymentException e) {
            throw new CorruptedDataPersistenceException(
                    "Payment data is database is corrupted for payment id: " + paymentEntity.getId(), e);
        }
    }
}
