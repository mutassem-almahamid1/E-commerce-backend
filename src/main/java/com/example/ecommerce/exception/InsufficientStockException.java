package com.example.ecommerce.exception;

import com.example.ecommerce.exception.excptions.CustomException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends CustomException {

    public InsufficientStockException(String productName, int requestedQuantity, int availableStock) {
        super(String.format("الكمية المطلوبة للمنتج '%s' غير متوفرة. المطلوب: %d، المتوفر: %d",
              productName, requestedQuantity, availableStock),
              HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(Long productId, int requestedQuantity, int availableStock) {
        super(String.format("الكمية المطلوبة للمنتج رقم %d غير متوفرة. المطلوب: %d، المتوفر: %d",
              productId, requestedQuantity, availableStock),
              HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK");
    }
}
