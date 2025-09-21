package com.example.ecommerce.service;

import com.example.ecommerce.model.dto.request.ProductRequest;
import com.example.ecommerce.model.dto.response.ProductResponse;
import com.example.ecommerce.util.MessageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ProductService {
    Page<ProductResponse> search(String name , String category, Pageable pageable);
    @Transactional
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long id);
    ProductResponse getProductByName(String name);
    List<ProductResponse> getProductByCategory(String name);
    List<ProductResponse> getAllProducts();
    @Transactional
    MessageResponse update(Long id, ProductRequest request);
    @Transactional
    MessageResponse DeleteProduct(Long id);

}