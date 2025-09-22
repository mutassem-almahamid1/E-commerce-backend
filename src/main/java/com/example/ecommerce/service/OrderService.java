package com.example.ecommerce.service;

import com.example.ecommerce.model.dto.response.OrderResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {

    @Transactional
    OrderResponse createOrderFromCart(String email);

    @Transactional
    OrderResponse cancelOrder(Long orderId, String email);

    List<OrderResponse> getUserOrders(String email);

    List<OrderResponse> listAll();
}