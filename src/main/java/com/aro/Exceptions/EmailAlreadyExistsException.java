package com.aro.Exceptions;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String mess) {
        super(mess);
    }
}
