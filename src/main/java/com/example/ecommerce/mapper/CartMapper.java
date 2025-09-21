package com.example.ecommerce.mapper;

import com.example.ecommerce.model.dto.response.CartItemResponse;
import com.example.ecommerce.model.dto.response.CartResponse;
import com.example.ecommerce.model.entity.Cart;
import com.example.ecommerce.model.entity.CartItem;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setQuantity(cartItem.getQuantity());
        response.setProduct(ProductMapper.toProductResponse(cartItem.getProduct()));
        response.setSubtotal(cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        return response;
    }

    public static CartResponse toCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());

        Set<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(CartMapper::toCartItemResponse)
                .collect(Collectors.toSet());
        response.setItems(itemResponses);

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotal(total);

        return response;
    }
}