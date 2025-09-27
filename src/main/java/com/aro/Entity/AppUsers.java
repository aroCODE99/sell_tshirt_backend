package com.aro.Entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nimbusds.openid.connect.sdk.claims.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity(name = "app_users")
@ToString
public class AppUsers {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> userRoles = new HashSet<>();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Orders> orders;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private WishList wishList;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Addresses> addresses;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Payments> payments;

    public AppUsers() {
    }

    public AppUsers(String email, String username, String password, Set<Roles> userRoles) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.userRoles = userRoles;
    }

    public AppUsers(String email, String username, String password, String oauthProvider, Set<Roles> userRoles) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.oauthProvider = oauthProvider;
        this.userRoles = userRoles;
    }

    @PrePersist()
    private void prePersistCallback() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate()
    private void preUpdateCallback() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addCart(Cart cart) {
        this.cart = cart;
        cart.setUser(this);
    }

    public void addWishList(WishList wl) {
        this.wishList = wl;
        wl.setUser(this);
    }

    public void addAddress(Addresses address) {
        this.addresses.add(address);
        address.setUser(this);
    }

    public void addPayment(Payments payments) {
        this.payments.add(payments);
        payments.setUser(this);
    }

}