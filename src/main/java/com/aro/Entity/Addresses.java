package com.aro.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Addresses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressType;

    private String country;

    private int postalCode;

    private String city;

    private String streetName;

    private String landmark;

    private String name;

    private String phoneNumber;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUsers user;

}
