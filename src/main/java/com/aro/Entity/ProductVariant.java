package com.aro.Entity;

import com.aro.Enums.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString(exclude = "product")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Products product;

    // now i don't think this is going to be the string
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Size size; // this is main thing

    @Column(nullable = false)
    private int quantity; // this is not quantity as in product quantity this product stock

    public ProductVariant(Size size, int quantity) {
        this.size = size;
        this.quantity = quantity;
    }

    public ProductVariant(Products product, Size size, int quantity) {
        this.product = product;
        this.size = size;
        this.quantity = quantity;
    }


}
