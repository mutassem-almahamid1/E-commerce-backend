package com.example.ecommerce.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = true, message = "Price must be non-negative")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

}