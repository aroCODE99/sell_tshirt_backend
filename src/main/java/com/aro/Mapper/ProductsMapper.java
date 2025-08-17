package com.aro.Mapper;

import com.aro.DTOs.ProductsDto;
import com.aro.Entity.Category;
import com.aro.Entity.ProductVariant;
import com.aro.Entity.Products;
import com.aro.Enums.Size;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class ProductsMapper {

    // make the mapper for the toEntity

    public static Products toEntity(ProductsDto dto) {
        Products product = new Products();
        product.setCategory(new Category(dto.getCategoryType()));
        product.setColor(dto.getColor());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(BigDecimal.valueOf(Long.parseLong(dto.getPrice())));

        product.setProductVariants(dto.getSizes().stream()
            .map((s) -> new ProductVariant(product, Size.valueOf(s), 50))
            .collect(Collectors.toSet()));

        return product;
    }
}