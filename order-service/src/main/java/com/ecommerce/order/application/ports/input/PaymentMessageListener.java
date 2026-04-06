package com.ecommerce.order.application.ports.input;

import com.ecommerce.order.application.dto.PaymentFailedCommand;
import com.ecommerce.order.application.dto.PaymentSucceededCommand;

public interface PaymentMessageListener {
    void paymentSucceeded(PaymentSucceededCommand command);

    void paymentFailed(PaymentFailedCommand command);
}
