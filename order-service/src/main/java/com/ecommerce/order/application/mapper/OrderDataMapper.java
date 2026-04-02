package com.ecommerce.order.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.dto.OrderDTO;
import com.ecommerce.order.application.dto.OrderItemDTO;
import com.ecommerce.order.domain.entity.Order;

@Component
public class OrderDataMapper {

    public OrderDTO orderToOrderDTO(Order order) {
        List<OrderItemDTO> orderItems = order.getItems().stream().map(item -> new OrderItemDTO(
                item.getProductId().getId(),
                item.getQuantity(),
                item.getPrice().getAmount())).toList();

        return new OrderDTO(
                order.getId().getId(),
                order.getCustomerId().getId(),
                order.getCreatedAt(),
                order.getStatus(),
                orderItems);
    }
}
