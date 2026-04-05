package com.ecommerce.payment.domain;

import java.util.Optional;

import com.ecommerce.payment.domain.entity.CustomerCredit;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.event.PaymentEvent;
import com.ecommerce.payment.domain.event.PaymentFailedEvent;
import com.ecommerce.payment.domain.event.PaymentSucceededEvent;

public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment, Optional<CustomerCredit> customerCredit) {
        
        if (!customerCredit.isPresent()) {
            payment.markAsFailed();
            return new PaymentFailedEvent(payment, "Customer credit not found");
        }

        CustomerCredit credit = customerCredit.get();
        if (credit.getCreditAmount().isLessThan(payment.getAmount())) {
            payment.markAsFailed();
            return new PaymentFailedEvent(payment, "Insufficient customer credit");
        }

        credit.subtractCreditAmount(payment.getAmount());
        payment.markAsSuccess();

        return new PaymentSucceededEvent(payment);
    }
}

