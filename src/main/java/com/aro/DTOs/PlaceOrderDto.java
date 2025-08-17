package com.aro.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceOrderDto {

    private Long productId;

    private int quantity;

    private String size;
}
