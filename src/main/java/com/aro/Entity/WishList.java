package com.aro.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @ToString(exclude = "user")
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUsers user;

    @JsonManagedReference
    @OneToMany(mappedBy = "wishList", cascade = CascadeType.ALL)
    private Set<WishListItems> wishListItemsSet;

    public WishList(AppUsers user) {
        this.user = user;
    }

    public void addToWishList(WishListItems w) {
        wishListItemsSet.add(w);
        w.setWishList(this);
    }

    public void removeFromWishList(WishListItems item) {
        wishListItemsSet.remove(item);
        item.setWishList(null);
    }

}
