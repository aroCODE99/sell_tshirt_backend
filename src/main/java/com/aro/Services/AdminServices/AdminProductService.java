package com.aro.Services.AdminServices;

import com.aro.DTOs.ProductsDto;
import com.aro.Entity.OrderProduct;
import com.aro.Entity.Products;
import com.aro.Exceptions.ProductNotFoundException;
import com.aro.Exceptions.ResourceNotFoundException;
import com.aro.Mapper.ProductsMapper;
import com.aro.Repos.CartProductRepo;
import com.aro.Repos.OrderProductsRepo;
import com.aro.Repos.ProductsRepo;
import com.aro.Repos.WishListItemsRepo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminProductService {

    private static final Logger log = LoggerFactory.getLogger(AdminProductService.class);
    private final ProductsRepo productsRepo;

    private final OrderProductsRepo orderProductsRepo;

    private final CartProductRepo cartProductRepo;

    private final WishListItemsRepo wishListItemsRepo;

    private final Cloudinary cloudinary;

    public AdminProductService(ProductsRepo productsRepo, OrderProductsRepo orderProductsRepo, CartProductRepo cartProductRepo, WishListItemsRepo wishListItemsRepo, Cloudinary cloudinary) {
        this.productsRepo = productsRepo;
        this.orderProductsRepo = orderProductsRepo;
        this.cartProductRepo = cartProductRepo;
        this.wishListItemsRepo = wishListItemsRepo;
        this.cloudinary = cloudinary;
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
                String publicId = "products/" + UUID.randomUUID();
                // Convert MultipartFile to a byte array first
                byte[] fileBytes = dto.getImgUrl().getBytes();

                // Upload using the byte array instead of InputStream
                Map<?, ?> res = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "auto",
                        "folder", "products"
                    )
                );
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

    public ResponseEntity<?> test() {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                ObjectUtils.emptyMap()
            );

            return ResponseEntity.ok("Cloudinary connection successful: " + result.get("url"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Cloudinary connection failed: " + e.getMessage());
        }
    }

}
