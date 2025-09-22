package com.example.ecommerce.exception.excptions;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends CustomException {
    public InsufficientStockException(String productName, int requestedQuantity, int availableStock) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
              productName, requestedQuantity, availableStock),
              HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }
}
