package org.example.commercepayment.domain.order.dto;

public record OrderCheckoutResponse(
        Long orderId,
        String oderNumber,
        String portonePaymentId,
        int totalPrice,
        int usePoint,
        int pgAmount,
        String orderName,
        String status
) {}
