package com.example.ecommerce.mapper;

import com.example.ecommerce.model.dto.response.OrderItemResponse;
import com.example.ecommerce.model.dto.response.OrderResponse;
import com.example.ecommerce.model.entity.Order;
import com.example.ecommerce.model.entity.OrderItem;

import java.util.Collections;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductName(orderItem.getProduct() != null ? orderItem.getProduct().getName() : null);
        response.setQuantity(orderItem.getQuantity());
        response.setPrice(orderItem.getPrice());
        return response;
    }

    public static OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        response.setOrderDate(order.getCreatedAt());
        response.setItems(order.getOrderItems() == null
                ? Collections.emptySet()
                : order.getOrderItems().stream()
                .map(OrderMapper::toOrderItemResponse)
                .collect(Collectors.toSet()));
        return response;
    }

}