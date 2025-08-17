package com.aro.Services;

import com.aro.DTOs.ErrorResponse;
import com.aro.DTOs.SuccessResponse;
import com.aro.Entity.AppUsers;
import com.aro.Entity.Products;
import com.aro.Entity.WishList;
import com.aro.Entity.WishListItems;
import com.aro.Exceptions.EmptyWishListException;
import com.aro.Exceptions.ProductNotFoundException;
import com.aro.Exceptions.ResourceNotFoundException;
import com.aro.Repos.AuthRepo;
import com.aro.Repos.ProductsRepo;
import com.aro.Repos.WishListItemsRepo;
import com.aro.Repos.WishListRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WishListService {

    private final WishListRepo wishListRepo;

    private final WishListItemsRepo wishListItemsRepo;

    private final JwtService jwtService;

    private final ProductsRepo productsRepo;

    private final AuthRepo authRepo;

    private final Logger log = LoggerFactory.getLogger(WishListService.class);

    public WishListService(WishListRepo wishListRepo, WishListItemsRepo wishListItemsRepo, JwtService jwtService, ProductsRepo productsRepo, AuthRepo authRepo) {
        this.wishListRepo = wishListRepo;
        this.wishListItemsRepo = wishListItemsRepo;
        this.jwtService = jwtService;
        this.productsRepo = productsRepo;
        this.authRepo = authRepo;
    }

    @Transactional
    public ResponseEntity<?> addToWishList(String authHeader, Long productId) {
        Long userId = jwtService.getUserId(authHeader);

        // just for testing users that do not have the wishlist created
        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not in the database")
        );

        Products product = productsRepo.findById(productId).orElseThrow(
            ProductNotFoundException::new
        );

        WishList userWishList = user.getWishList();

        if (userWishList == null) {
            userWishList = createWishList(user);
        }

        // now checking if the product has already been added or not
        if (userWishList.getWishListItemsSet() != null) {
            WishListItems existingItem = userWishList.getWishListItemsSet().stream()
                .filter(w -> w.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

            if (existingItem != null) {
                return ResponseEntity.ok().body(new SuccessResponse("Item already wishListed", LocalDateTime.now().toString()));
            }
        }

        WishListItems wishListItems = new WishListItems();
        wishListItems.setWishList(userWishList);
        wishListItems.setProduct(product);
        userWishList.addToWishList(wishListItems);

        wishListRepo.save(userWishList);
        return ResponseEntity.ok().body(new SuccessResponse("Item wishListed", LocalDateTime.now().toString()));
    }

    public ResponseEntity<?> getWishList(String authHeader) {
        Long userId = jwtService.getUserId(authHeader);

        Optional<WishList> wishList = wishListRepo.findByUserId(userId);
        if (wishList.isPresent()) {
            return ResponseEntity.ok().body(wishList.get());
        }

        return ResponseEntity.badRequest().body("WishList not created");
    }

    @Transactional
    public ResponseEntity<?> removeFromWishList(String authHeader, Long wishListItemId) {
        if (wishListItemId == null || wishListItemId < 0) {
            return ResponseEntity.badRequest().body("Invalid id");
        }

        Long userId = jwtService.getUserId(authHeader);

        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not in the Database")
        );

        WishList wishList = wishListRepo.findByUserId(userId).orElse(
            createWishList(user)
        );

        if (wishList.getWishListItemsSet() != null) {
            WishListItems wishListItems = wishList.getWishListItemsSet().stream()
                .filter(w -> w.getId().equals(wishListItemId))
                .findFirst()
                .orElse(null);

            if (wishListItems != null) {
                wishList.removeFromWishList(wishListItems);
            } else {
                throw new ResourceNotFoundException("Product is not wishListed for the given product id");
            }
        } else {
            throw new EmptyWishListException("WishList items are empty");
        }

        wishListRepo.save(wishList);
        return ResponseEntity.ok().body(new SuccessResponse("Product removed from the wishList", LocalDateTime.now().toString()));
    }

    @Transactional
    public WishList createWishList(AppUsers user) {
        return wishListRepo.save(new WishList(user));
    }

}
