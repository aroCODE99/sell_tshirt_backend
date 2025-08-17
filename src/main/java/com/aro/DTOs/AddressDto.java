package com.aro.DTOs;

import lombok.*;

@Getter @Setter @ToString
public class AddressDto {

    private String name;

    private String phoneNumber;

    private String streetName;

    private String landmark;

    private String city;

    private int postalCode;

    private String addressType;

    private String country;

}
