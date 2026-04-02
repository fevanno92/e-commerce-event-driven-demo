package com.ecommerce.order.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.application.dto.CreateOrderCommand;
import com.ecommerce.order.application.dto.GetOrderResponse;
import com.ecommerce.order.application.dto.OrderItemDTO;
import com.ecommerce.order.application.exception.InvalidProductException;
import com.ecommerce.order.application.ports.input.OrderApplicationService;
import com.ecommerce.order.application.ports.output.OrderEventPublisher;
import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.application.ports.output.ProductClient;
import com.ecommerce.order.domain.OrderDomainService;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.entity.Product;
import com.ecommerce.order.domain.event.OrderCreatedEvent;
import com.ecommerce.order.domain.valueobject.CustomerId;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.ProductId;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderDomainService orderDomainService;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Autowired
    public OrderApplicationServiceImpl(OrderDomainService orderDomainService, ProductClient productClient, OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public void createOrder(CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {}", createOrderCommand);

        Map<ProductId, Product> products = getProductsFromProductService(createOrderCommand);
        Order order = createOrderFromCreateOrderCommand(createOrderCommand);
        
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitializeOrder(order, products);

        orderRepository.save(order);

        // TODO Publish domain events in a more robust way, e.g. implement the outbox pattern to ensure reliable event publishing
        orderEventPublisher.publish(orderCreatedEvent);
    }
    
    @Override
    public List<GetOrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.getAllOrders();

        return orders.stream().map((order) -> {             
            List<OrderItemDTO> orderItems = order.getItems().stream().map(item -> new OrderItemDTO(
                    item.getProductId().getId(),
                    item.getQuantity(),
                    item.getPrice().getAmount())).toList();

            return new GetOrderResponse(
                order.getId().getId(),
                order.getCustomerId().getId(),
                order.getCreatedAt(),
                order.getStatus(),
                orderItems
            );
         }).toList();
    }

    private Map<ProductId, Product> getProductsFromProductService(CreateOrderCommand createOrderCommand) {        
        Map<ProductId, Product> products = new HashMap<>();
        for (var item : createOrderCommand.items()) {
            var productOpt = productClient.getProductById(item.productId());
            if (productOpt.isEmpty()) {
                log.warn("Product with ID {} not found", item.productId());
                throw new InvalidProductException("Product with ID " + item.productId() + " not found");
            }
            if (!productOpt.get().isValid()) {
                log.warn("Product with ID {} is invalid", item.productId());
                throw new InvalidProductException("Product with ID " + item.productId() + " is invalid");
            }
            products.put(new ProductId(item.productId()), productOpt.get());
        }
        return products;
    }

    private Order createOrderFromCreateOrderCommand(CreateOrderCommand createOrderCommand) {
        List<OrderItem> orderItems = createOrderCommand.items().stream()
                .map((item) -> new OrderItem(new ProductId(item.productId()), item.quantity(), new Money(item.price())))
                .toList();
        return new Order(new CustomerId(createOrderCommand.customerId()), orderItems);        
    }

}
