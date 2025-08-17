package com.aro.DTOs;

import lombok.*;

@Data
public class PaymentCallbackDto {
    private long selectedAddressId;
    private long userOrderId;
    private String orderCreationId;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}