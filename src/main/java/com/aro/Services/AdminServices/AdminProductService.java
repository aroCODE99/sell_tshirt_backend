package com.aro.Services.AdminServices;

import com.aro.DTOs.ProductsDto;
import com.aro.DTOs.SuccessDataResponse;
import com.aro.Entity.Products;
import com.aro.Exceptions.ProductNotFoundException;
import com.aro.Exceptions.ResourceNotFoundException;
import com.aro.Mapper.ProductsMapper;
import com.aro.Repos.CartProductRepo;
import com.aro.Repos.OrderProductsRepo;
import com.aro.Repos.ProductsRepo;
import com.aro.Repos.WishListItemsRepo;
import com.aro.Services.CloudinaryService;
import com.razorpay.Product;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AdminProductService {

    private static final Logger log = LoggerFactory.getLogger(AdminProductService.class);
    private final ProductsRepo productsRepo;

    private final OrderProductsRepo orderProductsRepo;

    private final CartProductRepo cartProductRepo;

    private final WishListItemsRepo wishListItemsRepo;

    private final CloudinaryService cloudinaryService;

    public AdminProductService(ProductsRepo productsRepo, OrderProductsRepo orderProductsRepo, CartProductRepo cartProductRepo, WishListItemsRepo wishListItemsRepo,
                               CloudinaryService cloudinaryService) {
        this.productsRepo = productsRepo;
        this.orderProductsRepo = orderProductsRepo;
        this.cartProductRepo = cartProductRepo;
        this.wishListItemsRepo = wishListItemsRepo;
        this.cloudinaryService = cloudinaryService;
    }

    public ResponseEntity<?> makeProductFeature(Long productId) {
        Products product = productsRepo.findById(productId).orElseThrow(
            () -> new ResourceNotFoundException("Product not in database with the id")
        );
        product.setFeatured(!product.isFeatured());
        Products savedProduct = productsRepo.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    // instead of doing the hard delete we could do soft delete
    @Transactional
    public ResponseEntity<?> deleteProduct(Long productId) {
        // now how will i be able to delete
        Products productToDelete = productsRepo.findById(productId).orElseThrow(
            ProductNotFoundException::new
        );
        productToDelete.setDeleted(true);
        productsRepo.save(productToDelete);
        log.info("Product deleted Successfully...");
        return ResponseEntity.ok().body(productToDelete);
    }

    @Transactional
    public ResponseEntity<?> createProduct(ProductsDto dto) throws IOException {
        try {
            Products products = ProductsMapper.toEntity(dto);

            if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
                Map<?, ?> res = cloudinaryService.uploadFile(dto.getImgUrl(), "products");
                String secureUrl = String.valueOf(res.get("secure_url"));
                products.setImgPath(secureUrl);
                products.setCloudinaryPublicId(String.valueOf(res.get("public_id")));
                log.info("Successfully uploaded image to Cloudinary: {}", secureUrl);
            }

            Products savedProduct = productsRepo.save(products);
            return ResponseEntity.ok().body(savedProduct);
        } catch (IOException e) {
            log.error("IO error during file upload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("File upload failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error during product creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating product: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> updateProduct(ProductsDto dto) throws IOException {
        Products product = productsRepo.findById(dto.getId())
            .orElseThrow(ProductNotFoundException::new);
        if (!product.getImgPath().isEmpty() && !product.getCloudinaryPublicId().isEmpty()) {
            cloudinaryService.destroyFile(product.getCloudinaryPublicId());
        }
        if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
            Map<?, ?> res = cloudinaryService.uploadFile(dto.getImgUrl(), "products");
            String secureUrl = String.valueOf(res.get("secure_url"));
            product.setImgPath(secureUrl);
            product.setCloudinaryPublicId(String.valueOf(res.get("public_id")));
        }

        ProductsMapper.updateProduct(product, dto);
        return ResponseEntity.ok(new SuccessDataResponse<Products>(
            "Product updated successfully",
            product,
            LocalDateTime.now().toString()
        ));
    }

    public ResponseEntity<?> test() {
        return cloudinaryService.test();
    }

}
