package com.ecommerce.order.infrastructure.repository;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.domain.entity.Order;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository orderRepository;

    public OrderRepositoryImpl(JpaOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void save(Order order) {
        OrderEntity orderEntity = new OrderEntity(
                order.getId().getId(),
                order.getCustomerId().getId(),
                order.getCreatedAt(),
                order.getStatus());

        order.getItems().forEach(item -> orderEntity.addItem(
                new OrderItemEntity(
                        item.getId().getId(),
                        item.getProductId().getId(),
                        item.getQuantity(),
                        item.getPrice().getAmount())));

        orderRepository.save(orderEntity);
    }
    
}
