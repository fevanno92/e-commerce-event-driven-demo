package com.ecommerce.product;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ecommerce.product.entities.Product;
import com.ecommerce.product.repositories.ProductRepository;

@Component
public class DefaultProducts implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Autowired
    public DefaultProducts(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            productRepository.save(new Product(UUID.fromString("33b525be-a713-4afb-a111-785d603b02e4"),"Laptop", "High performance laptop", 999.99));
            productRepository.save(new Product(UUID.fromString("1c9f898f-4718-44c7-8ba7-dcadee99e2f0"), "Smartphone", "Latest model smartphone", 699.99));
            productRepository.save(new Product(UUID.fromString("a64b7c22-7dbf-4e0c-800f-8a48d93f17c5"), "Headphones", "Noise-cancelling headphones", 199.99));
        }
    }

}
