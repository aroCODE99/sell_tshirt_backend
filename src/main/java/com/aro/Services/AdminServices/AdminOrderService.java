package com.aro.Services.AdminServices;

import com.aro.Entity.Orders;
import com.aro.Repos.OrdersRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderService {

    private final OrdersRepo ordersRepo;

    public AdminOrderService(OrdersRepo ordersRepo) {
        this.ordersRepo = ordersRepo;
    }

    public Page<Orders> findAllOrders(Pageable page) {
        return ordersRepo.findAll(page);
    }
}
