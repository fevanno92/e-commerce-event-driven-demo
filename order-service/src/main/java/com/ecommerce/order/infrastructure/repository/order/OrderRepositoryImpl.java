package com.ecommerce.order.infrastructure.repository.order;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.valueobject.CustomerId;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.OrderId;
import com.ecommerce.order.domain.valueobject.OrderItemId;
import com.ecommerce.order.domain.valueobject.ProductId;

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

    @Override
    public List<Order> getAllOrders() {
        List<OrderEntity> orderEntities = orderRepository.findAll();
        return orderEntities.stream().map(orderEntity -> {
            List<OrderItem> orderItems = orderEntity.getItems().stream().map(itemEntity -> new OrderItem(
                    new OrderItemId(itemEntity.getId()),
                    new OrderId(orderEntity.getId()),
                    new ProductId(itemEntity.getProductId()),
                    itemEntity.getQuantity(),
                    new Money(itemEntity.getPrice()))).toList();

            Order order = new Order(
                    new OrderId(orderEntity.getId()),
                    new CustomerId(orderEntity.getCustomerId()),
                    orderItems,
                    orderEntity.getCreatedAt(),
                    orderEntity.getStatus());

            return order;
        }).toList();
    }

}
