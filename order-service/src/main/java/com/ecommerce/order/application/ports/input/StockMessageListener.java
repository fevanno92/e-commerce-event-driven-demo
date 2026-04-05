package com.ecommerce.order.application.ports.input;

import com.ecommerce.order.application.dto.CancelOrderCommand;
import com.ecommerce.order.application.dto.ValidateOrderCommand;

public interface StockMessageListener {
    void validateOrder(ValidateOrderCommand command);

    void cancelOrder(CancelOrderCommand command);
}
