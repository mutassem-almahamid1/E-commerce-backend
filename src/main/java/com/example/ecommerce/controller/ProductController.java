package com.example.ecommerce.controller;

import com.example.ecommerce.model.dto.request.ProductRequest;
import com.example.ecommerce.model.dto.response.ProductResponse;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.util.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;


    @GetMapping("/{id}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> getProductByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @GetMapping("/search")
//    @PreAuthorize("isAuthenticated()")
    public Page<ProductResponse> searchProducts(@RequestParam String name,
                                                @RequestParam String category,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return productService.search(name, category, PageRequest.of(page, size));
    }

    @GetMapping("/category/{name}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductResponse>> getProductByCategory(@PathVariable String name) {
        return ResponseEntity.ok(productService.getProductByCategory(name));
    }

    @GetMapping("/all")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.createProduct(productRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.DeleteProduct(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateProduct(@PathVariable Long id,
                                    @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @GetMapping("/search/advanced")
//    @PreAuthorize("isAuthenticated()")
    public Page<ProductResponse> searchProductsAdvanced(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchAdvanced(name, category, minPrice, maxPrice, inStock,
                PageRequest.of(page, size));
    }

    @GetMapping("/search/by-name")
//    @PreAuthorize("isAuthenticated()")
    public Page<ProductResponse> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchByName(name, PageRequest.of(page, size));
    }

    @GetMapping("/search/by-price")
//    @PreAuthorize("isAuthenticated()")
    public Page<ProductResponse> searchByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchByPriceRange(minPrice, maxPrice, PageRequest.of(page, size));
    }

    @GetMapping("/available")
//    @PreAuthorize("isAuthenticated()")
    public Page<ProductResponse> getAvailableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.findAvailableProducts(PageRequest.of(page, size));
    }

    @GetMapping("/out-of-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProductResponse> getOutOfStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.findOutOfStockProducts(PageRequest.of(page, size));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(productService.findLowStockProducts(threshold));
    }

}
