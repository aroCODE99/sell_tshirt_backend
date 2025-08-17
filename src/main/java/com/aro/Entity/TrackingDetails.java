package com.aro.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class TrackingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders orders;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Addresses addresses;
}