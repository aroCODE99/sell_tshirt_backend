package com.aro.Exceptions;

import com.aro.DTOs.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("SERVER_ERROR", ex.getMessage(), LocalDateTime.now().toString()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseError(DataAccessException ex) {
        log.error("Database error", ex);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("DATABASE_ERROR", "Unable to save product", LocalDateTime.now().toString()));
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("EMAIL_ALREADY_EXISTS", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("PRODUCT_NOT_FOUND", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse("REFRESH_TOKEN_EXPIRED", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("USER_NOT_FOUND", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("RESOURCE_NOT_FOUND", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> HandleEmptyCartException(EmptyCartException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("CART_IS_EMPTY", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(EmptyWishListException.class)
    public ResponseEntity<ErrorResponse> handleEmptyWishListException(EmptyWishListException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("WISHLIST_IS_EMPTY", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        log.error("BAD_CREDENTIALS {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_CREDENTIALS", e.getMessage(), LocalDateTime.now().toString())
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("JWT_EXPIRED {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse("JWT_EXPIRED", e.getMessage(), LocalDateTime.now().toString())
        );
    }

}
