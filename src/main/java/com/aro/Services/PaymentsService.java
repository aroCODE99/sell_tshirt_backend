package com.aro.Services;

import com.aro.DTOs.PaymentCallbackDto;
import com.aro.Entity.*;
import com.aro.Enums.PaymentStatus;
import com.aro.Repos.AuthRepo;
import com.aro.Repos.OrdersRepo;
import com.aro.Repos.PaymentsRepo;
import com.nimbusds.openid.connect.sdk.claims.Address;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentsService {

    @Value("${razor.key.id}")
    private String KEY_ID;

    @Value("${razor.key.secret}")
    private String KEY_SECRET;

    private final AuthRepo authRepo;
    private final OrdersRepo ordersRepo;
    private final PaymentsRepo paymentsRepo;

    public PaymentsService(AuthRepo authRepo, OrdersRepo ordersRepo, PaymentsRepo paymentsRepo) {
        this.authRepo = authRepo;
        this.ordersRepo = ordersRepo;
        this.paymentsRepo = paymentsRepo;
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
            payment.setAmount(orders.getTotalAmount().add(BigDecimal.valueOf(99 + 50 + 99)));
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setMethod("ONLINE");
            payment.setOrder(orders);

            user.addPayment(payment);
            authRepo.save(user);

            TrackingDetails trackingDetails = new TrackingDetails();
            trackingDetails.setStatus("PENDING");
            trackingDetails.setAddresses(selectedAddress);
            orders.addTrackingDetails(trackingDetails);

            ordersRepo.save(orders);

            return true;
        } else {

            Payments payment = new Payments();
            payment.setRazorPaymentId(razorpayPaymentId);
            payment.setRazorOrderId(razorpayOrderId);
            payment.setAmount(orders.getTotalAmount().add(BigDecimal.valueOf(99 + 50 + 99)));
            payment.setStatus(PaymentStatus.FAILED);
            payment.setMethod("ONLINE");
            payment.setOrder(orders);

            user.addPayment(payment);
            authRepo.save(user);

            log.warn("Payment verification failed for Order ID: {}", userOrderId);
            return false;
        }
    }
}
