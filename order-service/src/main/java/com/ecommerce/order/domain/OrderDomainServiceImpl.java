package com.ecommerce.order.domain;

import java.math.BigDecimal;
import java.util.Map;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.entity.Product;
import com.ecommerce.order.domain.exception.InvalidOrderException;
import com.ecommerce.order.domain.valueobject.ProductId;

public class OrderDomainServiceImpl implements OrderDomainService {

    private static final BigDecimal PRICE_DELTA = new BigDecimal("0.1");

    @Override
    public void validateOrderProducts(Order order, Map<ProductId, Product> products) {        
        // make sure the price in the order is the same as the price in the product catalog
        for (OrderItem orderItem : order.getItems()) {
            Product product = products.get(orderItem.getProductId());

            if (product == null) {
                throw new InvalidOrderException("Product not found for ID: " + orderItem.getProductId());
            }

            BigDecimal priceDifference = orderItem.getPrice().getAmount().subtract(product.getPrice().getAmount()).abs();
            if (priceDifference.compareTo(PRICE_DELTA) > 0) {
                throw new InvalidOrderException("Price mismatch for product ID: " + orderItem.getProductId());
            }
        }        
    }
}
