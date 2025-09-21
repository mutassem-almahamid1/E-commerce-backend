package com.example.ecommerce.service.impl;

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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalArgumentException("Product with name '" + productRequest.getName() + "' already exists.");
        }

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + productRequest.getCategoryId()));

        Product product1 =ProductMapper.toProduct(productRequest,category);

        Product savedProduct = productRepository.save(product1);
        return ProductMapper.toProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return ProductMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse getProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(()-> new EntityNotFoundException("the product not found :"+name));
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
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

            existingProduct.setName(AssistantHelper.trimString(request.getName()));
            existingProduct.setDescription(AssistantHelper.trimString(request.getDescription()));
            existingProduct.setPrice(request.getPrice());
            existingProduct.setStockQuantity(request.getStockQuantity());
            existingProduct.setImageUrl(AssistantHelper.trimString(request.getImageUrl()));

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            existingProduct.setCategory(category);


        productRepository.save(existingProduct);
        return AssistantHelper.toMessageResponse("Successful update");
    }


    @Override
    @Transactional
    public MessageResponse DeleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return AssistantHelper.toMessageResponse("Successful delete");
    }
}