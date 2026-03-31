package com.ecommerce.order.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.order.application.dto.CreateOrderCommand;
import com.ecommerce.order.application.exception.InvalidProductException;
import com.ecommerce.order.application.ports.input.OrderApplicationService;
import com.ecommerce.order.application.ports.output.ProductClient;
import com.ecommerce.order.domain.OrderDomainService;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.entity.Product;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderApplicationServiceImpl implements OrderApplicationService {
    
    private final OrderDomainService orderDomainService;
    private final ProductClient productClient;

    @Autowired
    public OrderApplicationServiceImpl(OrderDomainService orderDomainService, ProductClient productClient) {
        this.orderDomainService = orderDomainService;
        this.productClient = productClient;
    }

    @Override
    public void createOrder(CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {}", createOrderCommand);

        List<Product> products = new ArrayList<>();
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
            products.add(productOpt.get());
        }       
    }

}
