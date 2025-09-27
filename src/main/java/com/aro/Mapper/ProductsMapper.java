package com.aro.Mapper;

import com.aro.DTOs.ProductsDto;
import com.aro.Entity.ProductVariant;
import com.aro.Entity.Products;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductsMapper {

    private static Set<ProductVariant> getProductVariants(ProductsDto dto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Integer> sizesMap =  objectMapper.readValue(dto.getSizes(),
            new TypeReference<Map<String, Integer>>(){}
        );
        return sizesMap.entrySet().stream()
            .map(entry -> new ProductVariant(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    }

    // OPT: updating should be optimized
    public static void updateProduct(Products updateThisProduct, ProductsDto dto) throws JsonProcessingException {
        // now we are updating the product
        updateThisProduct.setName(dto.getName());
        updateThisProduct.setDescription(dto.getDescription());
        updateThisProduct.setCategory(dto.getCategoryType());
        updateThisProduct.setColor(dto.getColor());
        updateThisProduct.setPrice(BigDecimal.valueOf(dto.getPrice()));
        updateThisProduct.setDiscountedPrice(getDiscountedPrice(dto.getPrice(), dto.getDiscount()));

        Set<ProductVariant> dtosProductVariant = getProductVariants(dto);
        Map<String, ProductVariant> dtoMap = dtosProductVariant.stream()
            .collect(Collectors.toMap(
                ProductVariant::getSize,
                pv -> pv
            ));

        Map<String, ProductVariant> removedProductVariants = new HashMap<>();
        for (ProductVariant pv: updateThisProduct.getProductVariants()) {
            var existInMap = dtoMap.get(pv.getSize());
            if (existInMap != null) {
                pv.setQuantity(existInMap.getQuantity());
                dtoMap.remove(pv.getSize());
            } else {
                removedProductVariants.put(pv.getSize(), pv);
            }
        }

        dtoMap.forEach((s, pv) -> {
            updateThisProduct.addProductVariant(pv);
        });

        removedProductVariants.forEach((s, pv) -> {
            pv.setQuantity(0);
        });
    }

    public static Products toEntity(ProductsDto dto) throws JsonProcessingException {
        Products products = new Products();
        Set<ProductVariant> sizesDto = getProductVariants(dto);
        sizesDto.forEach(System.out::println);
        products.setName(dto.getName());
        products.setDescription(dto.getDescription());
        products.setColor(dto.getColor());
        products.setCategory(dto.getCategoryType());
        products.setPrice(BigDecimal.valueOf(dto.getPrice()));
        products.setDiscountedPrice(getDiscountedPrice(dto.getPrice(), dto.getDiscount()));
        sizesDto.forEach(s -> s.setProduct(products));
        System.out.println(sizesDto);
        products.setProductVariants(sizesDto);

        return products;
    }

    private static BigDecimal getDiscountedPrice(double price, double discount) {
        BigDecimal bgPrice = BigDecimal.valueOf(price);
        BigDecimal discountPercent = BigDecimal.valueOf(discount)
            .divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = bgPrice.multiply(discountPercent);
        return bgPrice.subtract(discountAmount);
    }

}

