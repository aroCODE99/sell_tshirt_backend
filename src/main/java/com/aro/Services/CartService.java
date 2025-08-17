package com.aro.Services;

import com.aro.DTOs.AddToCartDto;
import com.aro.DTOs.SuccessResponse;
import com.aro.Entity.Cart;
import com.aro.Entity.CartProduct;
import com.aro.Entity.Products;
import com.aro.Exceptions.ProductNotFoundException;
import com.aro.Exceptions.ResourceNotFoundException;
import com.aro.Repos.CartProductRepo;
import com.aro.Repos.CartRepo;
import com.aro.Repos.ProductsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartProductRepo cartProductRepo;

    private final CartRepo cartRepo;

    private final ProductsRepo productsRepo;

    private final JwtService jwtService;

    public CartService(CartProductRepo cartProductRepo, CartRepo cartRepo, ProductsRepo productsRepo, JwtService jwtService) {
        this.cartProductRepo = cartProductRepo;
        this.cartRepo = cartRepo;
        this.productsRepo = productsRepo;
        this.jwtService = jwtService;
    }

    // same mistake not using the cascading
    @Transactional
    public ResponseEntity<?> addToCart(AddToCartDto addToCartDto, String authHeader) {
        Long userId = jwtService.getUserId(authHeader);

        // first find the product
        Products products = productsRepo.findById(addToCartDto.getProductId()).orElseThrow(
            ProductNotFoundException::new
        );

        // now we got the cart
        Cart userCart = cartRepo.findByUserId(userId);

        CartProduct existing = userCart.getCartProducts().stream()
            .filter(cp -> cp.getProduct().getId().equals(addToCartDto.getProductId()))
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + addToCartDto.getQuantity());
        } else {
            CartProduct actualCartProduct = new CartProduct();
            actualCartProduct.setProduct(products);
            actualCartProduct.setQuantity(addToCartDto.getQuantity());
            actualCartProduct.setSize(addToCartDto.getSize());
            userCart.addProductToCart(actualCartProduct);
        }

        cartRepo.save(userCart);
        return ResponseEntity.ok().body(new SuccessResponse("Item saved to cart", LocalDateTime.now().toString()));
    }

    public ResponseEntity<?> getCart(String token) {
        Long userId = jwtService.getUserId(token);

        Cart cart = cartRepo.findByUserId(userId);
        return ResponseEntity.ok().body(cart);
    }

    @Transactional
    public ResponseEntity<?> removeFromCart(String authHeader, Long cartProductId) {
        // now here this is expecting the cartProductId not the productId
        if (cartProductId == null || cartProductId < 0) {
            return ResponseEntity.badRequest().body("Invalid product ID.");
        }

        Long userId = jwtService.getUserId(authHeader);

        Cart cart = cartRepo.findByUserId(userId);

        CartProduct checkCartProduct = cart.getCartProducts().stream()
            .filter(cp -> cp.getId().equals(cartProductId))
            .findFirst()
            .orElse(null);

        if (checkCartProduct != null) {
           cart.removeFromCart(checkCartProduct);
        } else {
            throw new ResourceNotFoundException("Product is not in the Cart");
        }

        cartRepo.save(cart);
        return ResponseEntity.ok().body(new SuccessResponse("Product removed from the cart", LocalDateTime.now().toString()));
    }

}