package com.example.ecommerce.service;

import com.example.ecommerce.model.dto.request.CartItemRequest;
import com.example.ecommerce.model.dto.response.CartResponse;
import com.example.ecommerce.model.entity.Cart;
import com.example.ecommerce.util.MessageResponse;
import org.springframework.transaction.annotation.Transactional;

public interface CartService {
    @Transactional
    Cart createNewCartById(Long userId);

    @Transactional
    Cart createNewCartByEmail(String email);

    CartResponse addItemToCart(Long userId, CartItemRequest cartItemRequest);
    CartResponse getCartByUserId(Long userId);
    CartResponse getCartByEmail(String email);

    @Transactional
    CartResponse updateItem(String email, Long productId, CartItemRequest req);

    @Transactional
    MessageResponse deleteCartItem(Long cartItemId);

    @Transactional
    CartResponse removeItem(String email, Long productId);


    @Transactional
    MessageResponse clearCart(String email);
}