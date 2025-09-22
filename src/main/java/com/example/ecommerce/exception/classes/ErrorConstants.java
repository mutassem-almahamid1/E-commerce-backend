package com.example.ecommerce.exception.classes;

public final class ErrorConstants {

    // Authentication & Authorization
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";

    // Resource Management
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";

    // Validation
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String ILLEGAL_ARGUMENT = "ILLEGAL_ARGUMENT";
    public static final String TYPE_MISMATCH = "TYPE_MISMATCH";
    public static final String MISSING_PARAMETER = "MISSING_PARAMETER";
    public static final String MALFORMED_JSON = "MALFORMED_JSON";

    // Business Logic
    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    public static final String CART_EMPTY = "CART_EMPTY";
    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";

    // Database
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";

    // System
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String METHOD_NOT_SUPPORTED = "METHOD_NOT_SUPPORTED";
    public static final String ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";

    private ErrorConstants() {
        // Utility class
    }
}
