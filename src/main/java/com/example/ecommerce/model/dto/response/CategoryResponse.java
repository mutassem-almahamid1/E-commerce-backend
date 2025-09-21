package com.example.ecommerce.model.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String imageUrl;
}