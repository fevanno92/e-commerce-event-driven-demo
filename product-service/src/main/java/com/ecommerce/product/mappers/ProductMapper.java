package com.ecommerce.product.mappers;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.entities.Product;

public class ProductMapper {

    public static ProductDTO toProductDTO(Product product) {
        return new ProductDTO(
                product.getId().toString(),
                product.getName(),
                product.getDescription(),
                product.getPrice());
    }

    public static Product toProductEntity(CreateProductRequest createProductRequest) {
        return new Product(
                createProductRequest.name(),
                createProductRequest.description(),
                createProductRequest.price());
    }

}
