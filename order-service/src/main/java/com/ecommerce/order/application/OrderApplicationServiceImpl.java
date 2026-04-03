package com.ecommerce.order.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.application.dto.CreateOrderCommand;
import com.ecommerce.order.application.dto.OrderDTO;
import com.ecommerce.order.application.exception.InvalidProductException;
import com.ecommerce.order.application.mapper.OrderDataMapper;
import com.ecommerce.order.application.outbox.OutboxEventSerializer;
import com.ecommerce.order.application.outbox.OutboxMessage;
import com.ecommerce.order.application.ports.input.OrderApplicationService;
import com.ecommerce.order.application.ports.output.OrderMetrics;
import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.application.ports.output.OutboxRepository;
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
    private final OutboxRepository outboxRepository;
    private final OutboxEventSerializer outboxEventSerializer;
    private final OrderMetrics orderMetrics;
    private final OrderDataMapper orderDataMapper;

    @Autowired
    public OrderApplicationServiceImpl(OrderDomainService orderDomainService, ProductClient productClient,
            OrderRepository orderRepository, OutboxRepository outboxRepository,
            OutboxEventSerializer outboxEventSerializer, OrderMetrics orderMetrics, 
            OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.outboxEventSerializer = outboxEventSerializer;
        this.orderMetrics = orderMetrics;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional    
    public OrderDTO createOrder(CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {}", createOrderCommand);

        try {
            Map<ProductId, Product> products = getProductsFromProductService(createOrderCommand);
            Order order = createOrderFromCreateOrderCommand(createOrderCommand);
            
            OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitializeOrder(order, products);

            orderRepository.save(order);

            // use Outbox pattern to ensure reliable event publication
            OutboxMessage outboxMessage = outboxEventSerializer.createOutboxMessage(orderCreatedEvent);
            outboxRepository.save(outboxMessage);
            
            orderMetrics.recordOrderCreationSuccess();

            return orderDataMapper.orderToOrderDTO(order);
        } catch (InvalidProductException ex) {
            orderMetrics.recordOrderCreationFailure("INVALID_PRODUCT");
            throw ex;
        } catch (Exception ex) {
            orderMetrics.recordOrderCreationFailure("UNKNOWN_ERROR");
            throw ex;
        }
    }
    
    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.getAllOrders();
        return orders.stream().map(orderDataMapper::orderToOrderDTO).toList();
    }
                   
    private Map<ProductId, Product> getProductsFromProductService(CreateOrderCommand createOrderCommand) {        
        Map<ProductId, Product> products = new HashMap<>();
        for (var item : createOrderCommand.items()) {
            long startTime = System.currentTimeMillis();
            var productOpt = productClient.getProductById(item.productId());
            long duration = System.currentTimeMillis() - startTime;
            
            if (productOpt.isEmpty()) {
                log.warn("Product with ID {} not found", item.productId());
                orderMetrics.recordProductNotFound(item.productId());
                throw new InvalidProductException("Product with ID " + item.productId() + " not found");
            }
            
            orderMetrics.recordProductServiceCall(item.productId(), duration, true);
            
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
