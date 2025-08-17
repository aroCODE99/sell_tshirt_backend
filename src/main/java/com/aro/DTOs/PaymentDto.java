package com.aro.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class PaymentDto {

    private Long orderId;

    private String razor_order_id;

    private String razor_payment_id;

    private Long amount;

    private String paymentStatus;
}