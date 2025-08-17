package com.aro.Entity;

import com.aro.Enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Payment always belongs to a user
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUsers user;

    // Payment is tied to an order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false)
    private String method; // e.g. "RAZORPAY", "ONLINE"

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @Column(name = "razor_order_id")
    private String razorOrderId;

    @Column(name = "razor_payment_id")
    private String razorPaymentId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void setTimeStamps() {
        this.createdAt = LocalDateTime.now();
    }
}
