package com.example.ecommerce.exception.excptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: %s", resource, field, value),
              HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
