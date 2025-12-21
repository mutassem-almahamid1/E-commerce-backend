package com.example.ecommerce.service.impl;

import com.example.ecommerce.exception.excptions.DuplicateResourceException;
import com.example.ecommerce.exception.excptions.InsufficientStockException;
import com.example.ecommerce.exception.excptions.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.dto.request.ProductRequest;
import com.example.ecommerce.model.dto.response.ProductResponse;
import com.example.ecommerce.model.entity.Category;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.util.AssistantHelper;
import com.example.ecommerce.util.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> search(String name, String category, Pageable pageable) {
        return productRepository.search(name, category, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        Optional<Product> product = productRepository.findByName(productRequest.getName());
        if (product.isPresent()) {
            throw new DuplicateResourceException("Product", "name", productRequest.getName());
        }

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        Product product1 =ProductMapper.toProduct(productRequest,category);

        Product savedProduct = productRepository.save(product1);
        return ProductMapper.toProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse getProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "name", name));
        return ProductMapper.toProductResponse(product);
    }

    @Override
    public List<ProductResponse> getProductByCategory(String name) {
        List<Product> products = productRepository.findByCategoryName(name);

        return products
                .stream()
                .map(ProductMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MessageResponse update(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            existingProduct.setName(AssistantHelper.trimString(request.getName()));
            existingProduct.setDescription(AssistantHelper.trimString(request.getDescription()));
            existingProduct.setPrice(request.getPrice());
            existingProduct.setStockQuantity(request.getStockQuantity());
            existingProduct.setImageUrl(AssistantHelper.trimString(request.getImageUrl()));

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            existingProduct.setCategory(category);


        productRepository.save(existingProduct);
        return AssistantHelper.toMessageResponse("Successful update");
    }


    @Override
    @Transactional
    public MessageResponse DeleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
        return AssistantHelper.toMessageResponse("Successful delete");
    }


    public void checkStockAvailability(Long productId, int requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(product.getName(), requestedQuantity, product.getStockQuantity());
        }

    }
    public Product checkStockAvailabilityAndGetProduct(Long productId, int requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(product.getName(), requestedQuantity, product.getStockQuantity());
        }

        return product;
    }


    @Transactional
    public void reduceStock(Long productId, int quantity) {
        Product product = checkStockAvailabilityAndGetProduct(productId, quantity);

        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);

        productRepository.save(product);
    }

    @Transactional
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        int newStock = product.getStockQuantity() + quantity;
        product.setStockQuantity(newStock);

        productRepository.save(product);
    }

    public boolean isStockAvailable(Long productId, int requestedQuantity) {
        try {
            checkStockAvailability(productId, requestedQuantity);
            return true;
        } catch (InsufficientStockException | ResourceNotFoundException e) {
            return false;
        }
    }

    @Override
    public Page<ProductResponse> searchAdvanced(String name, String category,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              Boolean inStock, Pageable pageable) {

        return productRepository.search(name, category, minPrice, maxPrice, inStock, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> searchByName(String name, Pageable pageable) {

        if (name == null || name.trim().isEmpty()) {
            return getAllProductsPaged(pageable);
        }

        return productRepository.searchByName(name, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("الحد الأدنى للسعر لا يمكن أن يكون أكبر من الحد الأقصى");
        }

        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> findAvailableProducts(Pageable pageable) {

        return productRepository.findAvailableProducts(pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> findOutOfStockProducts(Pageable pageable) {

        return productRepository.findOutOfStockProducts(pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    public List<ProductResponse> findLowStockProducts(Integer threshold) {

        if (threshold == null || threshold <= 0) {
            threshold = 10; // القيمة الافتراضية
        }

        return productRepository.findLowStockProducts(threshold)
                .stream()
                .map(ProductMapper::toProductResponse)
                .collect(Collectors.toList());
    }


    private Page<ProductResponse> getAllProductsPaged(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toProductResponse);
    }
}