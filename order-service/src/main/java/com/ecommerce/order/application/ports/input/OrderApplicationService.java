package com.ecommerce.order.application.ports.input;

import com.ecommerce.order.application.dto.CreateOrderCommand;

public interface OrderApplicationService {
    public void createOrder(CreateOrderCommand createOrderCommand);
}
