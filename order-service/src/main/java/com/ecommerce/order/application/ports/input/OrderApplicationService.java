package com.ecommerce.order.application.ports.input;

import java.util.List;

import com.ecommerce.order.application.dto.CreateOrderCommand;
import com.ecommerce.order.application.dto.GetOrderResponse;

public interface OrderApplicationService {
    public void createOrder(CreateOrderCommand createOrderCommand);

    public List<GetOrderResponse> getAllOrders();
}
