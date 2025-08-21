package com.aro.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class WishListItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishList wishList;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;
}
