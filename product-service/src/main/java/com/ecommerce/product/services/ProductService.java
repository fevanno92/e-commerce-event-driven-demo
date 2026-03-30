package com.ecommerce.product.services;

import java.util.Collection;
import java.util.Optional;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.ProductDTO;

public interface ProductService {
    public ProductDTO createProduct(CreateProductRequest createProductRequest);

    public Optional<ProductDTO> getProductById(String id);

    public Collection<ProductDTO> getAllProducts();
}
