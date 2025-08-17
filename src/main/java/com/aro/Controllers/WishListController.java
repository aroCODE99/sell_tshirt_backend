package com.aro.Controllers;

import com.aro.Services.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishList")
public class WishListController {

    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping
    public ResponseEntity<?> getWishList(@RequestHeader("Authorization") String authHeader) {
        return wishListService.getWishList(authHeader);
    }

    @PostMapping("/addToWishList/{id}")
    public ResponseEntity<?> addToWishList(@RequestHeader("Authorization") String authHeader, @PathVariable("id") Long id) {
        return wishListService.addToWishList(authHeader, id);
    }

    @PostMapping("/removeFromWishList/{id}")
    public ResponseEntity<?> removeFromList(@RequestHeader("Authorization") String authHeader, @PathVariable("id") Long id) {
        return wishListService.removeFromWishList(authHeader, id);
    }

}
