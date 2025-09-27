package com.aro.Controllers.AdminControllers;

import com.aro.Entity.Orders;
import com.aro.Services.AdminServices.AdminOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final int PAGE_SIZE = 10;

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public Page<Orders> getOrders(@RequestParam("page") int pageNo) {
        Pageable page = PageRequest.of(pageNo, PAGE_SIZE, Sort.by("orderDate").descending());
        return adminOrderService.findAllOrders(page);
    }

}

