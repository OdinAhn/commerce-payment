package org.example.commercepayment.domain.order.dto;

public record OrderCheckoutResponse(
        Long orderId,
        String portonePaymentId,
        int totalPrice,
        String orderName,
        String status
) {}
