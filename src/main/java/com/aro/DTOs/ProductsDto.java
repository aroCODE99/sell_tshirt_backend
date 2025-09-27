package com.aro.DTOs;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @AllArgsConstructor @ToString
public class ProductsDto {
    private Long id;
    private String name;
    private double price;
    private double discount;
    private String description;
    private String categoryType;
    private String color;
    private MultipartFile imgUrl;
    private String sizes;
}
