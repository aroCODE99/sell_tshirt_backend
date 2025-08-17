package com.aro.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ToString.Exclude
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUsers user;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartProduct> cartProducts = new HashSet<>();

    public Cart(AppUsers user) {
        this.user = user;
    }

    public void addProductToCart(CartProduct cp) {
        cartProducts.add(cp);
        cp.setCart(this);
    }

    public void removeFromCart(CartProduct cp) {
        cartProducts.remove(cp);
        cp.setCart(null);
    }

}
