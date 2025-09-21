package com.example.ecommerce.model.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class CartResponse {
    private Long id;
    private Long userId;
    private Set<CartItemResponse> items;
    private BigDecimal total;

}