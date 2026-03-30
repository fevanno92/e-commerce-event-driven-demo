package com.ecommerce.product.services;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.entities.Product;
import com.ecommerce.product.mappers.ProductMapper;
import com.ecommerce.product.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDTO createProduct(CreateProductRequest createProductRequest) {
        Product product = ProductMapper.toProductEntity(createProductRequest);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toProductDTO(savedProduct);
    }

    @Override
    public Optional<ProductDTO> getProductById(String id) {
        return productRepository.findById(UUID.fromString(id))
                .map(ProductMapper::toProductDTO);
    }

    @Override
    public Collection<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toProductDTO)
                .toList();
    }
}
