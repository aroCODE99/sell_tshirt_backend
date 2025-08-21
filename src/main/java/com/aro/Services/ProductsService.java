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

    public ProductsService(ProductsRepo productsRepo, CategoryRepo categoryRepo) {
        this.productsRepo = productsRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Products> getAllProducts() {
        return productsRepo.findAll();
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

}
