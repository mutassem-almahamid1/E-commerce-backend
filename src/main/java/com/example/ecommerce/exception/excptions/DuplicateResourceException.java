package com.example.ecommerce.exception.excptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends CustomException {
    public DuplicateResourceException(String resourceName, String fieldName, Object value) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, value),
              HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}
