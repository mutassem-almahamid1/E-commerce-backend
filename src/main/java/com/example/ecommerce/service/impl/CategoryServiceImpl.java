package com.example.ecommerce.service.impl;

import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.model.dto.request.CategoryRequest;
import com.example.ecommerce.model.dto.response.CategoryResponse;
import com.example.ecommerce.model.entity.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.util.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Optional<Category> category = categoryRepository.findCategoryByNameIgnoreCase(categoryRequest.getName());
        if (category.isPresent()){
            throw new IllegalArgumentException("Category with name '" + categoryRequest.getName() + "' already exists.");
        }
        Category category1 = CategoryMapper.toCategory(categoryRequest);
        Category savedCategory = categoryRepository.save(category1);
        return CategoryMapper.toCategoryResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findCategoryById(id);
        if (category.isPresent()){
            return CategoryMapper.toCategoryResponse(category.get());
        } else {
            throw new IllegalArgumentException("Category with id '" + id + "' not found.");
        }
    }

    @Override
    public CategoryResponse getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findCategoryByNameIgnoreCase(name);
        if (category.isPresent()){
            return CategoryMapper.toCategoryResponse(category.get());
        } else {
            throw new IllegalArgumentException("Category with name '" + name + "' not found.");
        }
    }

    @Override
    public MessageResponse deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return new MessageResponse("Category with id '" + id + "' has been deleted.");
        } else {
            throw new IllegalArgumentException("Category with id '" + id + "' not found.");
        }
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> existingCategory = categoryRepository.findCategoryById(id);
        if (existingCategory.isPresent()) {
            Category categoryToUpdate = existingCategory.get();
            categoryToUpdate.setName(categoryRequest.getName());
            categoryToUpdate.setImageUrl(categoryRequest.getImageUrl());
            Category updatedCategory = categoryRepository.save(categoryToUpdate);
            return CategoryMapper.toCategoryResponse(updatedCategory);
        } else {
            throw new IllegalArgumentException("Category with id '" + id + "' not found.");
        }
    }
}