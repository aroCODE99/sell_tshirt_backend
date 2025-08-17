package com.aro.Exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("Product not available");
    }

}
