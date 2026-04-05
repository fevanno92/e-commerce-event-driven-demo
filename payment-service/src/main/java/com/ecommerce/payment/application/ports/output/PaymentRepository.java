package com.ecommerce.payment.application.ports.output;

import java.util.Optional;

import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.valueobject.OrderId;

public interface PaymentRepository {    
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(OrderId orderId);
}
