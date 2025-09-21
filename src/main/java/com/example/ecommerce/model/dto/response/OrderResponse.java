package com.example.ecommerce.model.dto.response;

import com.example.ecommerce.model.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private Set<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
}