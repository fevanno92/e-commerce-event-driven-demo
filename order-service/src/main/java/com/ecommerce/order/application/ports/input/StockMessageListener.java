package com.ecommerce.order.application.ports.input;

import com.ecommerce.order.application.dto.CancelOrderCommand;
import com.ecommerce.order.application.dto.CompleteOrderCommand;
import com.ecommerce.order.application.dto.FailOrderCommand;
import com.ecommerce.order.application.dto.ValidateOrderCommand;

public interface StockMessageListener {
    void validateOrder(ValidateOrderCommand command);

    void cancelOrder(CancelOrderCommand command);

    void completeOrder(CompleteOrderCommand command);

    void failOrder(FailOrderCommand command);
}
