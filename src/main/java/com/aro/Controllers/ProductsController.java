package com.aro.Controllers;

import com.aro.DTOs.ProductsDto;
import com.aro.Entity.Category;
import com.aro.Entity.Products;
import com.aro.Services.ProductsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping
    public List<Products> getAllProducts() {
        return productsService.getAllProducts();
    }

    @GetMapping("/getAllCategories")
    public List<Category> getAllCategories() {
        return productsService.getAllCategories();
    }

}
