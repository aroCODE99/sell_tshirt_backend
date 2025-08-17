package com.aro.Controllers;

import com.aro.DTOs.AddToCartDto;
import com.aro.Services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String authHeader) {
        return cartService.getCart(authHeader);
    }

    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String authHeader, @RequestBody AddToCartDto addToCartDto) {
        System.out.println(addToCartDto);
        return cartService.addToCart(addToCartDto, authHeader);
    }

    @PostMapping("/removeFromCart/{id}")
    public ResponseEntity<?> removeFromCart(@RequestHeader("Authorization") String authHeader, @PathVariable("id") Long id) {
        return cartService.removeFromCart(authHeader, id);
    }


}
