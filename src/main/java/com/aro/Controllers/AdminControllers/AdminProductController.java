package com.aro.Controllers.AdminControllers;

import com.aro.DTOs.ProductsDto;
import com.aro.Services.AdminServices.AdminProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
        @ModelAttribute ProductsDto dto) throws IOException {
        System.out.println(dto);
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    // TODO: who's updating the products to the database
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
        @ModelAttribute ProductsDto dto) throws IOException {
        return ResponseEntity.ok(productService.updateProduct(dto));
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
