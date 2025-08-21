package com.aro.Controllers.AdminControllers;

import com.aro.DTOs.ProductsDto;
import com.aro.Entity.Products;
import com.aro.Mapper.ProductsMapper;
import com.aro.Services.AdminServices.AdminProductService;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/admin/products")
public class AdminProductController {

    private final AdminProductService productService;

    public AdminProductController(AdminProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String hello() {
        return "Hello Admin";
    }

    @PostMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
        @RequestParam("name") String name,
        @RequestParam("price") double price,
        @RequestParam("description") String description,
        @RequestParam("categoryType") String categoryType,
        @RequestParam("color") String color,
        @RequestParam(value = "imgUrl", required = false) MultipartFile file,
        @RequestParam("sizes") List<String> sizes) throws IOException {
        ProductsDto dto = new ProductsDto(
            null,
            name,
            price,
            description,
            categoryType,
            color,
            file,
            sizes
        );
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @PostMapping("/makeFeatureProduct/{id}")
    public ResponseEntity<?> makeFeatureProducts(@PathVariable Long id) {
        return ResponseEntity.ok(productService.makeProductFeature(id));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return productService.test();
    }

}
