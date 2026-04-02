package com.ecommerce.product.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.exceptions.ProductNotFoundException;
import com.ecommerce.product.services.ProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;        
    }

    @GetMapping
    public Collection<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable("id") String id) {
        return productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid CreateProductRequest createProductRequest) {
        return productService.createProduct(createProductRequest);
    }

}
