package com.aro.Controllers;

import com.aro.Exceptions.EmptyCartException;
import com.aro.Services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestHeader("Authorization") String authHeader) {
        return orderService.getOrders(authHeader);
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestHeader("Authorization") String authHeader) throws EmptyCartException {
        return orderService.placeOrder(authHeader);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentOrder(@RequestHeader("Authorization") String authHeader) {
        return orderService.getRecentOrder(authHeader);
    }

}
