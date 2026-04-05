package com.ecommerce.payment.domain;

import java.util.Optional;

import com.ecommerce.payment.domain.entity.CustomerCredit;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.event.PaymentEvent;

public interface PaymentDomainService {
    PaymentEvent validateAndInitiatePayment(Payment payment, Optional<CustomerCredit> customerCredit);
}
