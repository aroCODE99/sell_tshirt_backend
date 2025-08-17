package com.aro.DTOs;

import com.aro.Enums.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AddToCartDto {

    private Long productId;

    private int quantity;

    private String size;
}
