package com.ecommerce.payment.application.ports.input;

import com.ecommerce.payment.application.dto.PaymentRequest;

public interface OrderMessageListener {    
    void processPayment(PaymentRequest request);
}
