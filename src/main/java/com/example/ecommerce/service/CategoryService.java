package com.example.ecommerce.service;

import com.example.ecommerce.model.dto.request.CategoryRequest;
import com.example.ecommerce.model.dto.response.CategoryResponse;
import com.example.ecommerce.util.MessageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    @Transactional
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse getCategoryByName(String name);
    @Transactional
    MessageResponse deleteCategory(Long id);
    @Transactional
    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);

}