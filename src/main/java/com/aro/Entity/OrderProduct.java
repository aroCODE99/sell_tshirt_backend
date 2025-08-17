package com.aro.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    private int quantity;

    @Column(nullable = false)
    private String selectedSize;

    private BigDecimal priceAtPurchase;

    public OrderProduct(Orders order, Products product, int quantity, BigDecimal priceAtPurchase, String size) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.selectedSize = size;
    }

    public OrderProduct(Products products, int quantity, BigDecimal priceAtPurchase) {
        this.product = products;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

}
