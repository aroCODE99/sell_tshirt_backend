package com.aro.Exceptions;

public class ProductSizeError extends RuntimeException {
    public ProductSizeError(String message) {
        super(message);
    }
}
