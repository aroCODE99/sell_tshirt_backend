package com.aro.Controllers;

import com.aro.DTOs.ProductsDto;
import com.aro.Services.AdminServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminServices adminServices;

    public AdminController(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello Admin";
    }

    @PostMapping("/admin/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductsDto product) {
        return ResponseEntity.ok("Hello making the admin panel");
    }

}
