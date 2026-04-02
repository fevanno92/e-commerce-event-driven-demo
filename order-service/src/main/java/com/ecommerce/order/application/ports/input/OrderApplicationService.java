package com.ecommerce.order.application.ports.input;

import java.util.List;

import com.ecommerce.order.application.dto.CreateOrderCommand;
import com.ecommerce.order.application.dto.OrderDTO;

public interface OrderApplicationService {
    public OrderDTO createOrder(CreateOrderCommand createOrderCommand);

    public List<OrderDTO> getAllOrders();
}
