package com.aro.Services;

import com.aro.DTOs.ProductsDto;
import com.aro.Entity.Category;
import com.aro.Entity.Products;
import com.aro.Mapper.ProductsMapper;
import com.aro.Repos.CategoryRepo;
import com.aro.Repos.ProductsRepo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductsService {

    private static final Logger log = LoggerFactory.getLogger(ProductsService.class);
    private final ProductsRepo productsRepo;

    private final CategoryRepo categoryRepo;

    private final Cloudinary cloudinary;

    public ProductsService(ProductsRepo productsRepo, CategoryRepo categoryRepo, Cloudinary cloudinary) {
        this.productsRepo = productsRepo;
        this.categoryRepo = categoryRepo;
        this.cloudinary = cloudinary;
    }

    public List<Products> getAllProducts() {
        return productsRepo.findAll();
    }

    @Transactional
    public ResponseEntity<?> createProduct(ProductsDto dto) throws IOException {
        Products products = ProductsMapper.toEntity(dto);

        if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
            // Validate before upload (size/type) – see below
            String publicId = "products/" + UUID.randomUUID();

            // this need the checking of the path
            Map<?, ?> res;
            try (InputStream in = dto.getImgUrl().getInputStream()) {
                res = cloudinary.uploader().upload(
                    in,
                    ObjectUtils.asMap(
                        "public_id", publicId,
                        "resource_type", "auto", // auto-detect image/video
                        "folder", "products"     // optional, if you prefer grouping
                    )
                );
            } catch (IOException e) {
                throw new FileUploadException("Failed to upload to Cloudinary", e);
            }

            // Prefer secure_url over url
            String secureUrl = String.valueOf(res.get("secure_url"));
            products.setImgPath(secureUrl);
            products.setCloudinaryPublicId(String.valueOf(res.get("public_id")));
        }

        return ResponseEntity.ok().body(productsRepo.save(products));
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

}
