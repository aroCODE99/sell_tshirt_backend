package com.aro.Services;

import com.aro.DTOs.PlaceOrderDto;
import com.aro.Entity.*;
import com.aro.Enums.OrderStatus;
import com.aro.Exceptions.EmptyCartException;
import com.aro.Exceptions.ProductSizeError;
import com.aro.Exceptions.ResourceNotFoundException;
import com.aro.Repos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    private static final BigDecimal PLATFORM_FEE = BigDecimal.valueOf(20);
    private static final BigDecimal DELIVERY_FEE = BigDecimal.valueOf(99);
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = BigDecimal.valueOf(2000);

    private final OrdersRepo ordersRepo;

    private final JwtService jwtService;

    private final AuthRepo authRepo;

    private final CartRepo cartRepo;

    private final ProductsRepo productsRepo;

    public OrderService(OrdersRepo ordersRepo, JwtService jwtService, AuthRepo authRepo, CartRepo cartRepo, ProductsRepo productsRepo) {
        this.ordersRepo = ordersRepo;
        this.jwtService = jwtService;
        this.authRepo = authRepo;
        this.cartRepo = cartRepo;
        this.productsRepo = productsRepo;
    }

    @Transactional
    public ResponseEntity<?> placeOrder(String authHeader) throws EmptyCartException {
        Long userId = jwtService.getUserId(authHeader);
        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not present in database")
        );
        Cart cart = cartRepo.findByUserId(userId);
        if (cart.getCartProducts().isEmpty()) {
            throw new EmptyCartException("Cart is currently Empty");
        }
        final Orders saveThisOrder = createOrder(user, cart);
        cart.getCartProducts().clear();
        cartRepo.save(cart); // this should be clearing the cartProducts
        // now let's add these products to the orderProduct
        return ResponseEntity.ok().body(saveThisOrder);
    }

    @Transactional
    public ResponseEntity<?> placeOrder(String authHeader, PlaceOrderDto orderDto) {
        Long userId = jwtService.getUserId(authHeader);

        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not present in database")
        );

        Products products = productsRepo.findById(orderDto.getProductId()).orElseThrow(
            () -> new ResourceNotFoundException("Product is not there")
        );

        // now directly save the product in the order
        Orders saveThisOrder = new Orders();
        saveThisOrder.setUser(user);

        BigDecimal priceAtPurchase = products.getDiscountedPrice();
        OrderProduct op = new OrderProduct(saveThisOrder, products, orderDto.getQuantity(), priceAtPurchase, orderDto.getSize());
        saveThisOrder.addOrderProduct(op);
        saveThisOrder.setTotalAmount(priceAtPurchase); // as it has only one product

        Orders orders = ordersRepo.save(saveThisOrder);

        return ResponseEntity.ok().body(orders);
    }

    public Orders createOrder(AppUsers user, Cart cart) {
        Orders saveThisOrder = new Orders();
        saveThisOrder.setUser(user);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartProduct cp : cart.getCartProducts()) {
            Set<ProductVariant> productSizes = cp.getProduct().getProductVariants();
            ProductVariant productVariant = productSizes.stream()
                .filter(ps -> ps.getSize().equals(cp.getSize()))
                .findFirst()
                .orElseThrow(() -> new ProductSizeError("Product size not available"));

            if (productVariant.getQuantity() < cp.getQuantity()) {
                throw new ProductSizeError("Quantity is low");
            }

            BigDecimal priceAtPurchase = cp.getProduct().getDiscountedPrice();
            OrderProduct op = new OrderProduct(saveThisOrder, cp.getProduct(), cp.getQuantity(), priceAtPurchase, cp.getSize());
            saveThisOrder.addOrderProduct(op);
            totalAmount = totalAmount.add(priceAtPurchase.multiply(BigDecimal.valueOf(cp.getQuantity())));
        }

        totalAmount =  totalAmount.add(PLATFORM_FEE); // adding the platform fee

        if (totalAmount.compareTo(FREE_DELIVERY_THRESHOLD) <= 0) {
            totalAmount = totalAmount.add(DELIVERY_FEE); // for delivery fee
        }

        saveThisOrder.setTotalAmount(totalAmount);

        TrackingDetails tk = new TrackingDetails();
        tk.setStatus(OrderStatus.PENDING.name());
        saveThisOrder.addTrackingDetails(tk);

        return ordersRepo.save(saveThisOrder);
    }

    public ResponseEntity<Set<Orders>> getOrders(String authHeader) {
        Long userId = jwtService.getUserId(authHeader);

        AppUsers user = authRepo.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("User not in the database")
        );
        return ResponseEntity.ok().body(user.getOrders());
    }

    public ResponseEntity<?> getOrderById(Long id) {
        return ResponseEntity.ok(ordersRepo.findById(id));
    }

    public ResponseEntity<?> getRecentOrder(String authHeader) {
        Long userId = jwtService.getUserId(authHeader);
        return ResponseEntity.ok().body(ordersRepo.findLatestOrderByUserId(userId));
    }
}

// there are so much optimization like for now I don't need the ProductVariant in the Products which are child to other entities
// products should also be storing the discountPrice
