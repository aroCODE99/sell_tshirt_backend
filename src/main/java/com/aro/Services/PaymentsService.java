package com.aro.Services;

import com.aro.DTOs.PaymentCallbackDto;
import com.aro.Entity.*;
import com.aro.Enums.OrderStatus;
import com.aro.Enums.PaymentStatus;
import com.aro.Enums.PaymentType;
import com.aro.Repos.AuthRepo;
import com.aro.Repos.OrdersRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class PaymentsService {

    @Value("${razor.key.id}")
    private String KEY_ID;

    @Value("${razor.key.secret}")
    private String KEY_SECRET;

    private final AuthRepo authRepo;
    private final OrdersRepo ordersRepo;

    @Value("${delivery.fee}")
    private Long deliveryFee;

    @Value("${convenience.fee}")
    private Long convenienceFee;

    public PaymentsService(AuthRepo authRepo, OrdersRepo ordersRepo) {
        this.authRepo = authRepo;
        this.ordersRepo = ordersRepo;
    }

    public String createOrder(int amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // in paise
        orderRequest.put("currency", "INR");

        Order order = razorpay.orders.create(orderRequest);
        log.info("Created Razorpay order: {}", Optional.ofNullable(order.get("id")));

        return order.toString();
    }

    public boolean processPaymentCallback(PaymentCallbackDto dto) throws RazorpayException {
        long addressId = dto.getSelectedAddressId();
        long userOrderId = dto.getUserOrderId();
        String razorpayOrderId = dto.getRazorpayOrderId();
        String razorpayPaymentId = dto.getRazorpayPaymentId();
        String razorpaySignature = dto.getRazorpaySignature();

        Orders orders = ordersRepo.findById(userOrderId).orElseThrow(
            () -> new IllegalArgumentException("Order not found with ID: " + userOrderId)
        );

        AppUsers user = orders.getUser();

        Addresses selectedAddress = user.getAddresses().stream().filter(a -> a.getId() == addressId).findFirst().orElseThrow(
            () -> new IllegalArgumentException("Address not found")
        );

        String generatedSignature = razorpayOrderId + "|" + razorpayPaymentId;
        boolean isValid = Utils.verifySignature(generatedSignature, razorpaySignature, KEY_SECRET);

        if (isValid) {
            log.info("Payment verified for Order ID: {}", userOrderId);

            Payments payment = new Payments();
            payment.setRazorPaymentId(razorpayPaymentId);
            payment.setRazorOrderId(razorpayOrderId);
            payment.setAmount(orders.getTotalAmount().add(BigDecimal.valueOf(deliveryFee + convenienceFee)));
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setMethod(PaymentType.ONLINE.name());
            payment.setOrder(orders);

            user.addPayment(payment);
            authRepo.save(user);

            TrackingDetails trackingDetails = orders.getTrackingDetails();

            if (trackingDetails == null) {
                trackingDetails = new TrackingDetails();
                trackingDetails.setStatus(OrderStatus.ORDER_CONFIRMED.name());
                trackingDetails.setAddresses(selectedAddress);
                orders.addTrackingDetails(trackingDetails);
            }

            trackingDetails.setStatus(OrderStatus.ORDER_CONFIRMED.name());
            trackingDetails.setAddresses(selectedAddress);
            ordersRepo.save(orders);

            return true;
        } else {
            // payment failed
            Payments payment = new Payments();
            payment.setRazorPaymentId(razorpayPaymentId);
            payment.setRazorOrderId(razorpayOrderId);
            payment.setAmount(orders.getTotalAmount().add(BigDecimal.valueOf(deliveryFee + convenienceFee)));
            payment.setStatus(PaymentStatus.FAILED);
            payment.setMethod(PaymentType.ONLINE.name());
            payment.setOrder(orders);

            user.addPayment(payment);
            authRepo.save(user);

            log.warn("Payment verification failed for Order ID: {}", userOrderId);
            return false;
        }
    }
}
