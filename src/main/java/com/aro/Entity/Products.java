package com.aro.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = true)
    private BigDecimal discountedPrice = BigDecimal.ZERO;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column
    private String category;

    @Column(nullable = false)
    private String imgPath;

    @Column(nullable = false)
    private String cloudinaryPublicId;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isFeatured = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductVariant> productVariants;

    // Constructor for required fields
    public Products(String name, BigDecimal price, String description, String category,
                    String color, String imgPath) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.color = color;
        this.imgPath = imgPath;
    }

    // Constructor for required fields
    public Products(String name, BigDecimal price, String description, String category,
                    String color, String imgPath, Set<ProductVariant> productVariants) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.color = color;
        this.imgPath = imgPath;
        this.productVariants = productVariants;
    }

    public void addProductVariant(ProductVariant pv) {
        pv.setProduct(this);
        this.productVariants.add(pv);
    }

}