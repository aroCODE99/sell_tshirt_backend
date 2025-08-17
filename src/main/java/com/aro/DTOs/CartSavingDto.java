package com.aro.DTOs;

import com.aro.Entity.Products;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartSavingDto {

    private Products product;
    private int quantity;

    public CartSavingDto(Products product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

}
