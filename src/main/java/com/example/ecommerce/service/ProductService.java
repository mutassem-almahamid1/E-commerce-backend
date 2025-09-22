package com.example.ecommerce.service;

import com.example.ecommerce.model.dto.request.ProductRequest;
import com.example.ecommerce.model.dto.response.ProductResponse;
import com.example.ecommerce.util.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Page<ProductResponse> search(String name, String category, Pageable pageable);

    Page<ProductResponse> searchAdvanced(String name, String category,
                                       BigDecimal minPrice, BigDecimal maxPrice,
                                       Boolean inStock, Pageable pageable);

    Page<ProductResponse> searchByName(String name, Pageable pageable);

    Page<ProductResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<ProductResponse> findAvailableProducts(Pageable pageable);

    Page<ProductResponse> findOutOfStockProducts(Pageable pageable);

    List<ProductResponse> findLowStockProducts(Integer threshold);

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