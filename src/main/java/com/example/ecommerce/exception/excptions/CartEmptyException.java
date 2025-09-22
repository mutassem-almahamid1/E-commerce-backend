package com.example.ecommerce.exception.excptions;

import org.springframework.http.HttpStatus;

public class CartEmptyException extends CustomException {
    public CartEmptyException() {
        super("Cart is empty", HttpStatus.BAD_REQUEST, "CART_EMPTY");
    }

    public CartEmptyException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "CART_EMPTY");
    }
}
