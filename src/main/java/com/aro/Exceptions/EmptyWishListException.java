package com.aro.Exceptions;

public class EmptyWishListException extends RuntimeException {

    public EmptyWishListException(String message) {
        super(message);
    }
}
