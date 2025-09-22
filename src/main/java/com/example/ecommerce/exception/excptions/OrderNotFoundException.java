package com.example.ecommerce.exception.excptions;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends CustomException {
    public OrderNotFoundException(Long orderId) {
        super(String.format("Order not found with ID: %d", orderId),
              HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }

    public OrderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }
}
