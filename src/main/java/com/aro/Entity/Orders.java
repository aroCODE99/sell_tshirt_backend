package com.aro.Entity;

import com.aro.Repos.OrderProductsRepo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUsers user;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private TrackingDetails trackingDetails;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderProduct> orderProducts = new HashSet<>();

    private BigDecimal totalAmount;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Orders(AppUsers user, BigDecimal totalAmount) {
        this.user = user;
        this.totalAmount = totalAmount;
    }

    @PrePersist
    public void onCreate() {
        this.orderDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addOrderProduct(OrderProduct op) {
        orderProducts.add(op);
    }

    public void addTrackingDetails(TrackingDetails tk) {
        tk.setOrders(this);
        this.setTrackingDetails(tk);
    }

}
