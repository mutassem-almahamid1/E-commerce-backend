package com.example.ecommerce.mapper;

import com.example.ecommerce.model.dto.request.ProductRequest;
import com.example.ecommerce.model.dto.response.ProductResponse;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.model.entity.Category;
import com.example.ecommerce.util.AssistantHelper;

import java.time.LocalDateTime;

public class ProductMapper {
    public static ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            response.setCategory(CategoryMapper.toCategoryResponse(product.getCategory()));
        }

        return response;
    }

    public static Product toProduct(ProductRequest productRequest, Category category) {
        Product product = new Product();

        product.setName(AssistantHelper.trimString(productRequest.getName()));
        product.setDescription(AssistantHelper.trimString(productRequest.getDescription()));
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setImageUrl(AssistantHelper.trimString(productRequest.getImageUrl()));
        product.setCreatedAt(LocalDateTime.now());

        product.setCategory(category);

        return product;
    }
}