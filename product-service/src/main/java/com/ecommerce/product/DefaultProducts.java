package com.ecommerce.product;

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
            productRepository.save(new Product("Laptop", "High performance laptop", 999.99));
            productRepository.save(new Product("Smartphone", "Latest model smartphone", 699.99));
            productRepository.save(new Product("Headphones", "Noise-cancelling headphones", 199.99));
        }
    }

}
