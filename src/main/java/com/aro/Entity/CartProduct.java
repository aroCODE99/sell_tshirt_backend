package com.aro.Entity;

import com.aro.Enums.Size;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor  @ToString
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;

    private int quantity; // like how much are we adding

    private String size;

    public CartProduct(Cart cart, Products product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

}
